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
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;

public final class SeqExporter implements SequenceExporterI {

    private final VisualizationGroupI vgroup;
    private final DistributionI<Long> dist;

    public SeqExporter(VisualizationGroupI vgroup, DistributionI<Long> dist) {
        this.vgroup = vgroup;
        this.dist = dist;
    }

    @Override
    public boolean export() {
        final ExportSeqWizardIterator iter = new ExportSeqWizardIterator(vgroup, dist);
        WizardDescriptor wiz = new WizardDescriptor(iter);
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Export sequences for " + vgroup.getDisplayName());
        
        Object notify = DialogDisplayer.getDefault().notify(wiz);
        
        if (notify == WizardDescriptor.CANCEL_OPTION) {
            return false;
        } else if (notify == WizardDescriptor.FINISH_OPTION) {

            final File target = iter.getSelectedFile();
            final Set<AttributeI> selectedAttributes = iter.getSelectedAttributes();
            
            if (target != null && target.exists()) {
                // ask if file should be overwritten, else return
                String msg = new StringBuilder("A file named ")
                        .append(target.getName())
                        .append(" already exists. Should this file be overwritten?")
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

                    // no attributes were selected for export
                    if (selectedAttributes.isEmpty()) {
                        return null;
                    }

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
                        if (fw != null) {
                            fw.close();
                        }
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
                    failed(downloader.getErrorMessage());
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
        public void failed(String reason) {
            super.failed(reason);
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
                    failed(pce.getNewValue().toString());
                    break;
                default:
                    super.propertyChange(pce);
                    break;
            }
        }
    }
}
