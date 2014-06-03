package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class UploadFile extends AbstractAction {

    private final FileNodeFactory fnf;

    public UploadFile(FileNodeFactory nf) {
        putValue(NAME, "Upload file");
        fnf = nf;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JFileChooser fchooser = new JFileChooser();

        // try to restore last directory selection
        String last = NbPreferences.forModule(JFileChooser.class).get("lastDirectory", null);
        if (last != null) {
            File f = new File(last);
            if (f.exists() && f.isDirectory()) {
                fchooser.setCurrentDirectory(f);
            }
        }
        if (fchooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        NbPreferences.forModule(JFileChooser.class).put("lastDirectory", fchooser.getCurrentDirectory().getAbsolutePath());

        final File localFile = fchooser.getSelectedFile();

        MGXMasterI master = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        final MGXFileI targetDir = Utilities.actionsGlobalContext().lookup(MGXFileI.class);
        final UploadBaseI uploader;
        try {
            uploader = master.File().createUploader(localFile, targetDir, localFile.getName());
            final MGXTask upTask = new MGXTask("Upload " + fchooser.getSelectedFile().getName()) {
                @Override
                public boolean process() {
                    return uploader.upload();
                }

                @Override
                public void finished() {
                    super.finished();
                    fnf.refreshChildren();
                }

                @Override
                public void failed() {
                    super.failed();
                    fnf.refreshChildren();
                }

                @Override
                public void propertyChange(PropertyChangeEvent pce) {
                    if (pce.getPropertyName().equals(UploadBaseI.NUM_ELEMENTS_SENT)) {
                        setStatus(String.format("%1$d bytes sent", pce.getNewValue()));
                    } else {
                        super.propertyChange(pce);
                    }
                }

                @Override
                public boolean isDeterminate() {
                    return true;
                }

                @Override
                public int getProgress() {
                    float complete = 1.0f * uploader.getNumElementsSent() / localFile.length();
                    return Math.round(100 * complete);
                }
            };
            uploader.addPropertyChangeListener(upTask);

            NonEDT.invoke(new Runnable() {
                @Override
                public void run() {
                    TaskManager.getInstance().addTask(upTask);
                }
            });
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    @Override
    public boolean isEnabled() {
        return (super.isEnabled() && RBAC.isUser());
    }
}
