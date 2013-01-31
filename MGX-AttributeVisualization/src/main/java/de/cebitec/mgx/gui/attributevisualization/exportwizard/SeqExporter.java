package de.cebitec.mgx.gui.attributevisualization.exportwizard;

import de.cebitec.mgx.client.datatransfer.DownloadBase;
import de.cebitec.mgx.client.datatransfer.SeqByAttributeDownloader;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.groups.SequenceExporterI;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.seqstorage.FastaWriter;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;

public final class SeqExporter implements SequenceExporterI {

    private final VisualizationGroup vgroup;
    private final Distribution dist;

    public SeqExporter(VisualizationGroup vgroup, Distribution dist) {
        this.vgroup = vgroup;
        this.dist = dist;
    }

    @Override
    public void export() {
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
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {

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
                    return;
                }
            }

            SwingWorker<FastaWriter, Void> worker = new SwingWorker<FastaWriter, Void>() {
                @Override
                protected FastaWriter doInBackground() throws Exception {
                    FastaWriter writer = new FastaWriter(target.getAbsolutePath());
                    Set<Attribute> selectedAttributes = p1.getSelectedAttributes();
                    List<String> attrs = new ArrayList<>();
                    for (Attribute a : selectedAttributes) {
                        attrs.add(a.getValue());
                    }

                    Map<SeqRun, Set<Attribute>> saveSet = vgroup.getSaveSet(attrs);
                    CountDownLatch latch = new CountDownLatch(saveSet.size());
                    for (Entry<SeqRun, Set<Attribute>> e : saveSet.entrySet()) {
                        MGXMaster m = (MGXMaster) e.getKey().getMaster();
                        SeqByAttributeDownloader downloader = m.Sequence().createDownloaderByAttributes(e.getValue(), writer);
                        MGXTask task = new DownloadTask("Export " + e.getKey().getName() + "to " + target.getName(), downloader, latch);
                        TaskManager.getInstance().addTask(task);
                    }
                    latch.await();
                    return writer;
                }

                @Override
                protected void done() {
                    FastaWriter fw;
                    try {
                        fw = get();
                        fw.close();
                    } catch (IOException | InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    super.done();
                }
            };
            worker.execute();
        }
    }

    private final class DownloadTask extends MGXTask {

        private final SeqByAttributeDownloader downloader;
        private final CountDownLatch latch;

        public DownloadTask(String taskName, SeqByAttributeDownloader downloader, CountDownLatch latch) {
            super(taskName);
            this.downloader = downloader;
            this.latch = latch;
        }

        @Override
        public void process() {
            downloader.addPropertyChangeListener(this);
            if (downloader.download()) {
                finished();
            } else {
                failed();
            }
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
                case DownloadBase.NUM_ELEMENTS_RECEIVED:
                    setStatus(String.format("%1$d sequences received", pce.getNewValue()));
                    break;
                case DownloadBase.TRANSFER_FAILED:
                    failed();
                    break;
                default:
                    super.propertyChange(pce);
                    break;
            }
        }

        @Override
        public boolean isDeterminate() {
            return false;
        }

        @Override
        public int getProgress() {
            return MGXTask.PROGRESS_UNKNOWN;
        }
    }
}
