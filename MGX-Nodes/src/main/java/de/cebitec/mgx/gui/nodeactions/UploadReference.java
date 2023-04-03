package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.swingutils.util.FileChooserUtils;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.Serial;
import javax.swing.AbstractAction;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class UploadReference extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public UploadReference() {
        super.putValue(NAME, "Upload EMBL/GenBank/FASTA reference");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String fName = FileChooserUtils.selectExistingFilename(new FileType[]{FileType.EMBLGENBANK, FileType.FAS});
        if (fName == null) {
            return;
        }
        File localFile = new File(fName);
        final MGXMasterI master = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);

        try {
            final UploadBaseI uploader = master.Reference().createUploader(localFile);

            final MGXTask run = new MGXTask("Upload " + fName) {

                private volatile boolean complete = false;

                @Override
                public boolean process() {
                    uploader.addPropertyChangeListener(this);
                    boolean ret = uploader.upload();

                    if (!ret) {
                        setStatus(uploader.getErrorMessage());
                    }
                    return ret;
                }

                @Override
                public void finished() {
                    if (!complete) {
                        complete = true;
                        super.finished();
                        uploader.removePropertyChangeListener(this);
                        master.childChanged();
                    }
                }

                @Override
                public void failed(String reason) {
                    if (!complete) {
                        complete = true;
                        super.failed(reason);
                        uploader.removePropertyChangeListener(this);
                        master.childChanged();
//                        parent.refreshChildren();
                    }
                }

                @Override
                public void propertyChange(PropertyChangeEvent pce) {
                    switch (pce.getPropertyName()) {
                        case TransferBaseI.NUM_ELEMENTS_TRANSFERRED:
                            setStatus(String.format("%1$d subregions sent", pce.getNewValue()));
                            break;
                        case TransferBaseI.TRANSFER_FAILED:
                            failed(pce.getNewValue().toString());
                            break;
                        case TransferBaseI.TRANSFER_COMPLETED:
                            setStatus("Import complete.");
                            break;
                        case TransferBaseI.MESSAGE:
                            setStatus(pce.getNewValue().toString());
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
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public boolean isEnabled() {
        return (super.isEnabled() && RBAC.isUser());
    }
}
