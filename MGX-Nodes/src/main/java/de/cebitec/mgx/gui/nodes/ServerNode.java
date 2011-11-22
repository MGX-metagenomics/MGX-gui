package de.cebitec.mgx.gui.nodes;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public class ServerNode extends AbstractNode {

    public ServerNode(Children children) {
        super(children);
    }

    public ServerNode(Children children, Lookup lookup) {
        super(children, lookup);
    }
}
