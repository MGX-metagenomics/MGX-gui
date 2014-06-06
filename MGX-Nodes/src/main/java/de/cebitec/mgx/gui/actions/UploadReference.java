package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.nodefactory.ReferenceNodeFactory;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.swingutils.util.FileChooserUtils;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import javax.swing.AbstractAction;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class UploadReference extends AbstractAction {

    private final ReferenceNodeFactory fnf;

    public UploadReference(ReferenceNodeFactory nf) {
        putValue(NAME, "Upload EMBL/GenBank");
        fnf = nf;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String fName = FileChooserUtils.selectExistingFilename(new FileType[]{FileType.EMBLGENBANK});
        if (fName == null) {
            return;
        }
        File localFile = new File(fName);
        final MGXMasterI master = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        final UploadBaseI uploader = master.Reference().createUploader(localFile);

        final MGXTask run = new MGXTask("Upload " + fName) {
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
    }

    @Override
    public boolean isEnabled() {
        return (super.isEnabled() && RBAC.isUser());
    }
}