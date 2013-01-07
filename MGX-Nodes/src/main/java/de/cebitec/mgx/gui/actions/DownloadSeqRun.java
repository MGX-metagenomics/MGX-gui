package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.client.datatransfer.DownloadBase;
import de.cebitec.mgx.client.datatransfer.SeqDownloader;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.seqstorage.FastaWriter;
import de.cebitec.mgx.sequence.SeqStoreException;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
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

    public DownloadSeqRun() {
        putValue(NAME, "Download FASTA");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final SeqRun seqrun = Utilities.actionsGlobalContext().lookup(SeqRun.class);

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
        
        // suggest a file name
        File suggestedName = new File(fchooser.getCurrentDirectory(), cleanupName(seqrun.getName()) + ".fas");
        int cnt = 1;
        while (suggestedName.exists()) {
            String newName = new StringBuilder(cleanupName(seqrun.getName()))
                                      .append(" (")
                                      .append(cnt++)
                                      .append(").fas")
                                      .toString();
            suggestedName = new File(fchooser.getCurrentDirectory(), newName);
        }
        fchooser.setSelectedFile(suggestedName);

        if (fchooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
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
            final FastaWriter writer = new FastaWriter(target.getAbsolutePath());

            MGXMaster master = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
            final SeqDownloader downloader = master.Sequence().createDownloader(seqrun.getId(), writer);

            MGXTask run = new MGXTask() {
                @Override
                public void process() {
                    downloader.addPropertyChangeListener(this);
                    boolean success = downloader.download();
                    if (!success) {
                        failed();
                    }
                }

                @Override
                public void finished() {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                        failed();
                    }
                }

                @Override
                public void failed() {
                    if (target.exists()) {
                        target.delete();
                    }
                }

                @Override
                public void propertyChange(PropertyChangeEvent pce) {
                    if (pce.getPropertyName().equals(DownloadBase.NUM_ELEMENTS_RECEIVED)) {
                        setStatus(String.format("%1$d sequences received", pce.getNewValue()));
                    }
                }

                @Override
                public boolean isDeterminate() {
                    return true;
                }

                @Override
                public int getProgress() {
                    int curSeqs = downloader.getProgress();
                    float fraction = 1.0f * curSeqs / seqrun.getNumSequences();
                    return Math.round(100 * fraction);
                }
            };

            TaskManager.getInstance().addTask("Export to " + fchooser.getSelectedFile().getName(), run);
        } catch (SeqStoreException ex) {
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
