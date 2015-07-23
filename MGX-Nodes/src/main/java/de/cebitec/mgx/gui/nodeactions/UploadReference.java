package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.TransferBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.nodefactory.MGXNodeFactoryBase;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.swingutils.util.FileChooserUtils;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import javax.swing.AbstractAction;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class UploadReference extends AbstractAction {

    private final MGXNodeFactoryBase parent;

    public UploadReference(MGXNodeFactoryBase nf) {
        putValue(NAME, "Upload EMBL/GenBank/FASTA reference");
        parent = nf;
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
                @Override
                public boolean process() {
                    return uploader.upload();
                }

                @Override
                public void finished() {
                    super.finished();
                    parent.refreshChildren();
                }

                @Override
                public void failed() {
                    super.failed();
                    parent.refreshChildren();
                }

                @Override
                public void propertyChange(PropertyChangeEvent pce) {
                    if (pce.getPropertyName().equals(TransferBaseI.NUM_ELEMENTS_TRANSFERRED)) {
                        setStatus(String.format("%1$d subregions sent", pce.getNewValue()));
                    } else {
                        super.propertyChange(pce);
                    }
                }
            };
            uploader.addPropertyChangeListener(run);

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
