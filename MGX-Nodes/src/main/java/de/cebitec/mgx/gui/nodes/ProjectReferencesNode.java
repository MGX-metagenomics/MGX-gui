package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.CreateDirectory;
import de.cebitec.mgx.gui.actions.UploadFile;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.nodefactory.ReferenceNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectReferencesNode extends MGXNodeBase<MGXMaster> {

    private ReferenceNodeFactory nf;

    public ProjectReferencesNode(final MGXMaster m) {
        this(new ReferenceNodeFactory(m), m);
        master = m;
        setDisplayName("Reference sequences");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectFiles.png");
    }

    private ProjectReferencesNode(ReferenceNodeFactory rnf, MGXMaster m) {
        super(Children.create(rnf, true), Lookups.fixed(m), m);
        nf = rnf;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action getPreferredAction() {
        return null;
    }

    @Override
    public Action[] getActions(boolean ctx) {
        return new Action[]{};
    }

    @Override
    public void updateModified() {
        //
    }
}
