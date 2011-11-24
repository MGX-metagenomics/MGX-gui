package de.cebitec.mgx.gui.nodes;

import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public class ProjectAnalysisTasksNode extends AbstractNode {

    public ProjectAnalysisTasksNode(Children children, Lookup lookup) {
        super(children, lookup);
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
