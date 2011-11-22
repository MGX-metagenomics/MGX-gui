package de.cebitec.mgx.gui.nodes;

import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public class ProjectNode extends AbstractNode {

    public ProjectNode(Children c) {
        super(c);
    }

    public ProjectNode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0];
    }
}
