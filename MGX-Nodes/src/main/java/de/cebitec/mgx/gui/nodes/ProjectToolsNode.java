package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.controller.MGXMaster;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectToolsNode extends MGXNodeBase<MGXMasterI, ProjectToolsNode> {

    public ProjectToolsNode(MGXMasterI m) {
        super(Children.LEAF, Lookups.singleton(m), m);
        master = m;
        setDisplayName("Tools");
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
    }

    @Override
    public void updateModified() {
        //
    }
}
