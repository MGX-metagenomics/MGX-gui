package de.cebitec.mgx.gui.nodes;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public class ProjectDataNode extends AbstractNode {

    public ProjectDataNode(Children children) {
        super(children);
        setDisplayName("Project Data");
    }

    public ProjectDataNode(Children children, Lookup lookup) {
        super(children, lookup);
        setDisplayName("Project Data");
    }
}
