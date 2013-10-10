package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.client.datatransfer.ReferenceUploader;
import de.cebitec.mgx.client.datatransfer.UploadBase;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.nodefactory.ReferenceNodeFactory;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.util.FileChooserUtils;
import de.cebitec.mgx.gui.util.FileType;
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
        MGXMaster master = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        final ReferenceUploader uploader = master.Reference().createUploader(localFile);

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
                if (pce.getPropertyName().equals(UploadBase.NUM_ELEMENTS_SENT)) {
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