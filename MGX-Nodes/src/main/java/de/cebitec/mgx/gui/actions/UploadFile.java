package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.client.upload.FileUploader;
import de.cebitec.mgx.client.upload.UploadBase;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
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

        File localFile = fchooser.getSelectedFile();


        MGXMaster master = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        final MGXFile targetDir = Utilities.actionsGlobalContext().lookup(MGXFile.class);
        final FileUploader uploader = master.File().createUploader(localFile, targetDir, localFile.getName());

        MGXTask run = new MGXTask() {
            @Override
            public void process() {
                boolean success = uploader.upload();
                if (!success) {
                    failed();
                }
            }

            @Override
            public void finished() {
                fnf.refreshChildren();
            }

            @Override
            public void failed() {
                fnf.refreshChildren();
            }

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if (pce.getPropertyName().equals(UploadBase.NUM_ELEMENTS_SENT)) {
                    setStatus(String.format("%1$d bytes sent", pce.getNewValue()));
                }
            }

            @Override
            public boolean isDeterminate() {
                return true;
            }

            @Override
            public int getProgress() {
                return uploader.getProgress();
            }
        };
        uploader.addPropertyChangeListener(run);

        TaskManager.getInstance().addTask("Upload " + fchooser.getSelectedFile().getName(), run);
    }

    @Override
    public boolean isEnabled() {
        return (super.isEnabled() && RBAC.isUser());
    }
}