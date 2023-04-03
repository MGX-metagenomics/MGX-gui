package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.swingutils.util.SuffixFilter;
import de.cebitec.mgx.seqcompression.SequenceException;
import de.cebitec.mgx.seqstorage.FASTQWriter;
import de.cebitec.mgx.seqstorage.FastaWriter;
import de.cebitec.mgx.seqstorage.PairedEndFASTQWriter;
import de.cebitec.mgx.seqstorage.QualityEncoding;
import de.cebitec.mgx.sequence.DNASequenceI;
import de.cebitec.mgx.sequence.SeqWriterI;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class DownloadSeqRun extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public DownloadSeqRun() {
        super.putValue(NAME, "Download FASTA/FASTQ");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final SeqRunI seqrun = Utilities.actionsGlobalContext().lookup(SeqRunI.class);

        JFileChooser fchooser = new JFileChooser();
        fchooser.setDialogType(JFileChooser.SAVE_DIALOG);

        // try to restore last directory selection
        String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
        if (last != null) {
            File f = new File(last);
            if (f.exists() && f.isDirectory() && f.canWrite()) {
                fchooser.setCurrentDirectory(f);
            }
        }

        boolean hasQuality;
        try {
            hasQuality = seqrun.getMaster().SeqRun().hasQuality(seqrun);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }

        // suggest a file name
        String suffix = hasQuality ? ".fastq" : ".fas";
        File suggestedName = new File(fchooser.getCurrentDirectory(), cleanupName(seqrun.getName()) + suffix);
        int cnt = 1;
        while (suggestedName.exists()) {
            String newName = new StringBuilder(cleanupName(seqrun.getName()))
                    .append(" (")
                    .append(cnt++)
                    .append(")")
                    .append(suffix)
                    .toString();
            suggestedName = new File(fchooser.getCurrentDirectory(), newName);
        }
        fchooser.setSelectedFile(suggestedName);
        FileFilter ff = hasQuality ? new SuffixFilter(FileType.FASTQ) : new SuffixFilter(FileType.FAS);
        fchooser.addChoosableFileFilter(ff);
        fchooser.setFileFilter(ff);

        if (fchooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fchooser.getCurrentDirectory().getAbsolutePath());

        final File target = fchooser.getSelectedFile();
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
            Object ret = DialogDisplayer.getDefault().notify(nd);
            if (!NotifyDescriptor.OK_OPTION.equals(ret)) {
                return;
            }
        }

        try {
            final SeqWriterI<? extends DNASequenceI> writer = hasQuality
                    ? seqrun.isPaired() ? new PairedEndFASTQWriter(target.getAbsolutePath(), QualityEncoding.Sanger)
                    : new FASTQWriter(target.getAbsolutePath(), QualityEncoding.Sanger)
                    : new FastaWriter(target.getAbsolutePath());

            final DownloadBaseI downloader = seqrun.getMaster().Sequence().createDownloader(seqrun, writer, false);

            final MGXTask run = new MGXTask("Export to " + fchooser.getSelectedFile().getName()) {
                @Override
                public boolean process() {
                    downloader.addPropertyChangeListener(this);
                    boolean ret = downloader.download();
                    downloader.removePropertyChangeListener(this);
                    if (!ret) {
                        setStatus(downloader.getErrorMessage());
                    }
                    return ret;
                }

                private volatile boolean complete = false;

                @Override
                public void finished() {
                    try {
                        writer.close();
                        super.finished();
                    } catch (Exception ex) {
                        failed(ex.getMessage());
                    }
                    complete = true;
                }

                @Override
                public void failed(String reason) {
                    try {
                        writer.close();
                    } catch (Exception ex) {
                    }
                    if (target.exists()) {
                        target.delete();
                    }
                    setStatus(downloader.getErrorMessage());
                    complete = true;
                    super.failed(reason);
                }

                @Override
                public void propertyChange(PropertyChangeEvent pce) {
                    switch (pce.getPropertyName()) {
                        case TransferBaseI.NUM_ELEMENTS_TRANSFERRED:
                            if (!complete) {
                                setStatus(NumberFormat.getInstance(Locale.US).format(pce.getNewValue()) + " sequences received");
                            }
                            break;
                        case DownloadBaseI.TRANSFER_FAILED:
                            failed(pce.getNewValue().toString());
                            break;
                        default:
                            super.propertyChange(pce);
                            break;
                    }
                }

                @Override
                public boolean isDeterminate() {
                    return true;
                }

                @Override
                public int getProgress() {
                    long curSeqs = downloader.getProgress();
                    float fraction = 1.0f * curSeqs / seqrun.getNumSequences();
                    return Math.round(100 * fraction);
                }
            };

            NonEDT.invoke(new Runnable() {
                @Override
                public void run() {
                    TaskManager.getInstance().addTask(run);
                }
            });
        } catch (SequenceException | MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private String cleanupName(String runName) {
        if (runName.contains(File.separator)) {
            runName = runName.replace(File.separator, "_");
        }
        return runName;
    }
}
