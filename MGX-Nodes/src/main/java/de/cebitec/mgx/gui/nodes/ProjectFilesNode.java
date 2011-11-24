package de.cebitec.mgx.gui.nodes;

import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public class ProjectFilesNode extends AbstractNode {

    public ProjectFilesNode(Children children, Lookup lookup) {
        super(children, lookup);
        setDisplayName("Project Files");
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
