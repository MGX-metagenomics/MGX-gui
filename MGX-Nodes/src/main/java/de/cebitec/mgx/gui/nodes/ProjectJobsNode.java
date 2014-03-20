package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectJobsNode extends MGXNodeBase<MGXMaster, ProjectJobsNode> {

    public ProjectJobsNode(MGXMaster m) {
        super(Children.LEAF, Lookups.singleton(m), m);
        master = m;
        setDisplayName("Jobs");
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
