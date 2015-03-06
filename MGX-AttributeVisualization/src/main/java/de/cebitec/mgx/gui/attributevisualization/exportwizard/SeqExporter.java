package de.cebitec.mgx.gui.attributevisualization.exportwizard;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.seqstorage.FastaWriter;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;

public final class SeqExporter implements SequenceExporterI {

    private final VisualizationGroupI vgroup;
    private final DistributionI dist;

    public SeqExporter(VisualizationGroupI vgroup, DistributionI dist) {
        this.vgroup = vgroup;
        this.dist = dist;
    }

    @Override
    public boolean export() {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<>();
        final ExportSeqWizardPanel1 p1 = new ExportSeqWizardPanel1();
        p1.setDistribution(dist);
        panels.add(p1);
        ExportSeqWizardPanel2 p2 = new ExportSeqWizardPanel2();
        p2.setVisualizationGroup(vgroup);
        panels.add(p2);
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Export sequences for " + vgroup.getName());
        Object notify = DialogDisplayer.getDefault().notify(wiz);
        if (notify == WizardDescriptor.CANCEL_OPTION) {
            return false;
        } else if (notify == WizardDescriptor.FINISH_OPTION) {

            final File target = p2.getSelectedFile();
            if (target.exists()) {
                // ask if file should be overwritten, else return
                String msg = new StringBuilder("A file named ")
                        .append(target.getName())
                        .append(" already exists. Should this ")
                        .append("file be overwritten?")
                        .toString();
                NotifyDescriptor nd = new NotifyDescriptor(msg,
                        "Overwrite file?",
                        NotifyDescriptor.OK_CANCEL_OPTION,
                        NotifyDescriptor.WARNING_MESSAGE,
                        null, null);
                if (!NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(nd))) {
                    return true;
                }
            }

            SwingWorker<SeqWriterI, Void> worker = new SwingWorker<SeqWriterI, Void>() {
                @Override
                protected SeqWriterI doInBackground() throws Exception {
                    SeqWriterI<DNASequenceI> writer = new FastaWriter(target.getAbsolutePath());
                    Set<AttributeI> selectedAttributes = p1.getSelectedAttributes();
                    List<String> attrs = new ArrayList<>();
                    for (AttributeI a : selectedAttributes) {
                        attrs.add(a.getValue());
                    }

                    Map<SeqRunI, Set<AttributeI>> saveSet = vgroup.getSaveSet(attrs);
                    CountDownLatch latch = new CountDownLatch(1);

                    List<DownloadBaseI> downloaders = new ArrayList<>();

                    for (Entry<SeqRunI, Set<AttributeI>> e : saveSet.entrySet()) {
                        MGXMasterI m = e.getKey().getMaster();
                        DownloadBaseI downloader = m.Sequence().createDownloaderByAttributes(e.getValue(), writer, false);
                        downloaders.add(downloader);
                    }

                    MGXTask task = new DownloadTask("Export to " + target.getName(), downloaders, latch);
                    TaskManager.getInstance().addTask(task);
                    latch.await();
                    return writer;
                }

                @Override
                protected void done() {
                    SeqWriterI fw;
                    try {
                        fw = get();
                        fw.close();
                    } catch (Exception ex) {
                        if (target.exists()) {
                            target.delete();
                        }
                        Exceptions.printStackTrace(ex);
                    }
                    super.done();
                }
            };
            worker.execute();
        }
        return true;
    }

    private final class DownloadTask extends MGXTask {

        private final List<DownloadBaseI> downloaders;
        private final CountDownLatch latch;
        private long numSeqs = 0;
        private long numSeqsTotal = 0;

        public DownloadTask(String taskName, List<DownloadBaseI> downloaders, CountDownLatch latch) {
            super(taskName);
            this.downloaders = downloaders;
            this.latch = latch;
        }

        @Override
        public boolean process() {
            boolean ret = false;
            for (DownloadBaseI downloader : downloaders) {
                downloader.addPropertyChangeListener(this);
                ret = downloader.download();
                downloader.removePropertyChangeListener(this);
                if (!ret) {
                    setStatus(downloader.getErrorMessage());
                    failed();
                }
                numSeqsTotal += numSeqs;
                numSeqs = 0;
            }
            return ret;
        }

        @Override
        public void finished() {
            super.finished();
            latch.countDown();
        }

        @Override
        public void failed() {
            super.failed();
            latch.countDown();
        }

        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            switch (pce.getPropertyName()) {
                case TransferBaseI.NUM_ELEMENTS_TRANSFERRED:
                    numSeqs = (long) pce.getNewValue();
                    setStatus(String.format("%1$d sequences received", numSeqsTotal + numSeqs));
                    break;
                case TransferBaseI.TRANSFER_FAILED:
                    failed();
                    break;
                default:
                    super.propertyChange(pce);
                    break;
            }
        }
    }
}
