package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.client.datatransfer.DownloadBase;
import de.cebitec.mgx.client.datatransfer.FileDownloader;
import de.cebitec.mgx.client.datatransfer.PluginDumpDownloader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
public class DownloadPluginDump extends AbstractAction {

    public DownloadPluginDump() {
        putValue(NAME, "Download Conveyor plugin definition");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final MGXMaster master = Utilities.actionsGlobalContext().lookup(MGXMaster.class);

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
        File suggestedName = new File(fchooser.getCurrentDirectory(), "MGX_plugins.xml");
        fchooser.setSelectedFile(suggestedName);

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
            final OutputStream writer = new FileOutputStream(target);
            final PluginDumpDownloader downloader = master.File().createPluginDumpDownloader(writer);

            final MGXTask run = new MGXTask("Save to " + fchooser.getSelectedFile().getName()) {
                @Override
                public boolean process() {
                    downloader.addPropertyChangeListener(this);
                    boolean ret = downloader.download();
                    downloader.removePropertyChangeListener(this);
                    return ret;
                }

                @Override
                public void finished() {
                    try {
                        writer.close();
                        super.finished();
                    } catch (IOException ex) {
                        failed();
                    }
                }

                @Override
                public void failed() {
                    if (target.exists()) {
                        target.delete();
                    }
                    super.failed();
                }

                @Override
                public void propertyChange(PropertyChangeEvent pce) {
                    switch (pce.getPropertyName()) {
                        case DownloadBase.NUM_ELEMENTS_RECEIVED:
                            setStatus(String.format("%1$d bytes received", pce.getNewValue()));
                            break;
                        case DownloadBase.TRANSFER_FAILED:
                            failed();
                            break;
                        default:
                            super.propertyChange(pce);
                            break;
                    }
                }
            };

            NonEDT.invoke(new Runnable() {
                @Override
                public void run() {
                    TaskManager.getInstance().addTask(run);
                }
            });
        } catch (MGXClientException | FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}