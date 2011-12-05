package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectAnalysisTasksNode extends MGXNodeBase {

    public ProjectAnalysisTasksNode(MGXMaster m) {
        super(Children.LEAF, Lookups.singleton(m));
        setDisplayName("Analysis Tasks");
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
    }
}
