package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.CreateDirectory;
import de.cebitec.mgx.gui.actions.DeleteFileOrDirectory;
import de.cebitec.mgx.gui.actions.UploadFile;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import de.cebitec.mgx.gui.wizard.sample.SampleWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class MGXDirectoryNode extends MGXNodeBase<MGXFile> {

    private FileNodeFactory nf = null;

    public MGXDirectoryNode(MGXMaster m, MGXFile f) {
        this(f, m, new FileNodeFactory(m, f));
    }

    private MGXDirectoryNode(MGXFile f, MGXMaster m, FileNodeFactory fnf) {
        super(Children.create(fnf, true), Lookups.fixed(m, f), f);
        master = m;
        setDisplayName(f.getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
        setShortDescription(f.getName());
        this.nf = fnf;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action getPreferredAction() {
        return null;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new CreateDirectory(nf), new DeleteFileOrDirectory(), new UploadFile(nf)};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getContent().getName());
    }
}
