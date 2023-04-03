package de.cebitec.mgx.gui.seqexporter;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.SequenceExporterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.seqstorage.FASTQWriter;
import de.cebitec.mgx.seqstorage.FastaWriter;
import de.cebitec.mgx.seqstorage.QualityEncoding;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;

public final class SeqExporter<T extends Number, U> implements SequenceExporterI {

    private final GroupI<U> vgroup;
    private final DistributionI<T> dist;
    private final Set<String> seenGeneNames;

    @SuppressWarnings("unchecked")
    public SeqExporter(GroupI<SeqRunI> vgroup, DistributionI<T> dist) {
        this.vgroup = (GroupI<U>) vgroup;
        this.dist = dist;
        this.seenGeneNames = null;
    }

    @SuppressWarnings("unchecked")
    public SeqExporter(GroupI<AssembledSeqRunI> vgroup, DistributionI<T> dist, Set<String> seenGeneNames) {
        this.vgroup = (GroupI<U>) vgroup;
        this.dist = dist;
        this.seenGeneNames = seenGeneNames;
    }

    @Override
    public boolean export() {
        final ExportSeqWizardIterator<T, U> iter = new ExportSeqWizardIterator<>(vgroup, dist);
        WizardDescriptor wiz = new WizardDescriptor(iter);
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Export sequences for " + vgroup.getDisplayName());

        Object notify = DialogDisplayer.getDefault().notify(wiz);

        if (notify == WizardDescriptor.CANCEL_OPTION) {
            return false;
        } else if (notify == WizardDescriptor.FINISH_OPTION) {

            final File target = iter.getSelectedFile();
            final Set<AttributeI> selectedAttributes = iter.getSelectedAttributes();
            final boolean hasQuality = iter.hasQuality();

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

            SwingWorker<SeqWriterI<?>, Void> worker = new SwingWorker<SeqWriterI<?>, Void>() {
                @Override
                @SuppressWarnings("unchecked")
                protected SeqWriterI<?> doInBackground() throws Exception {
                    SeqWriterI<? extends DNASequenceI> writer;
                    if (hasQuality) {
                        writer = new FASTQWriter(target.getAbsolutePath(), QualityEncoding.Sanger);
                    } else {
                        writer = new FastaWriter(target.getAbsolutePath());
                    }

                    // no attributes were selected for export
                    if (selectedAttributes.isEmpty()) {
                        return null;
                    }

                    List<String> attrs = new ArrayList<>();
                    for (AttributeI a : selectedAttributes) {
                        attrs.add(a.getValue());
                    }

                    Map<U, Set<AttributeI>> saveSet = vgroup.getSaveSet(attrs);
                    CountDownLatch latch = new CountDownLatch(1);

                    List<DownloadBaseI> downloaders = new ArrayList<>();

                    for (Entry<U, Set<AttributeI>> e : saveSet.entrySet()) {
                        U key = e.getKey();
                        MGXMasterI master = null;
                        if (key instanceof SeqRunI) {
                            master = ((SeqRunI) key).getMaster();
                            DownloadBaseI downloader = master.Sequence().createDownloaderByAttributes(e.getValue(), writer, false);
                            downloaders.add(downloader);
                        } else if (key instanceof AssembledSeqRunI) {
                            master = ((AssembledSeqRunI) key).getMaster();
                            // several assembled runs from the same assembly may refer to a gene 
                            // multiple times, so we need to filter for duplicates

                            DownloadBaseI downloader = master.AssembledRegion().createDownloaderByAttributes(e.getValue(), writer, false, seenGeneNames);
                            downloaders.add(downloader);
                        }

                    }

                    MGXTask task = new DownloadTask("Export to " + target.getName(), downloaders, latch);
                    TaskManager.getInstance().addTask(task);
                    latch.await();
                    return writer;
                }

                @Override
                protected void done() {
                    SeqWriterI<? extends DNASequenceI> fw;
                    try {
                        fw = get();
                        if (fw != null) {
                            fw.close();
                        }
                    } catch (SequenceException | IOException | InterruptedException | ExecutionException ex) {
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
                    if (numSeqs == 0) {
                        setStatus("Awaiting transfer..");
                    } else {
                        setStatus(NumberFormat.getInstance(Locale.US).format(numSeqsTotal + numSeqs) + " sequences received");
                    }
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
