package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectJobsNode extends MGXNodeBase<MGXMasterI> {

    public ProjectJobsNode(MGXMasterI m) {
        super(Children.LEAF, Lookups.singleton(m), m);
        super.setDisplayName("Jobs");
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
