package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.Semaphore;
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

    @Serial
    private static final long serialVersionUID = 1L;
    
    public UploadFile() {
        super.putValue(NAME, "Upload file(s)");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        JFileChooser fchooser = new JFileChooser();
        fchooser.setMultiSelectionEnabled(true);

        // TODO: restrict valid selection to files, not directories
        
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

        final MGXFileI targetDir = Utilities.actionsGlobalContext().lookup(MGXFileI.class);

        // sequential upload
        final Semaphore rateLimit = new Semaphore(1);

        for (final File localFile : fchooser.getSelectedFiles()) {

            // skip over directories
            if (localFile.isFile()) {

                final UploadBaseI uploader;
                try {
                    uploader = targetDir.getMaster().File().createUploader(localFile, targetDir, localFile.getName());
                    final MGXTask upTask = new MGXTask("Upload " + localFile.getName()) {
                        @Override
                        public boolean process() {
                            rateLimit.acquireUninterruptibly();
                            uploader.addPropertyChangeListener(this);
                            boolean ret = uploader.upload();
                            rateLimit.release();

                            if (!ret) {
                                setStatus(uploader.getErrorMessage());
                            }
                            return ret;
                        }

                        @Override
                        public void finished() {
                            super.finished();
                            uploader.removePropertyChangeListener(this);
                            targetDir.childChanged();
                        }

                        @Override
                        public void failed(String reason) {
                            super.failed(reason);
                            uploader.removePropertyChangeListener(this);
                            targetDir.childChanged();
                        }

                        @Override
                        public void propertyChange(PropertyChangeEvent pce) {
                            switch (pce.getPropertyName()) {
                                case TransferBaseI.NUM_ELEMENTS_TRANSFERRED:
                                    setStatus(NumberFormat.getInstance(Locale.US).format(pce.getNewValue()) + " bytes sent");
                                    break;
                                case TransferBaseI.TRANSFER_FAILED:
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
                            float complete = 1.0f * uploader.getNumElementsSent() / localFile.length();
                            return Math.round(100 * complete);
                        }
                    };

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
        }
    }

    @Override
    public boolean isEnabled() {
        return (super.isEnabled() && RBAC.isUser());
    }
}
