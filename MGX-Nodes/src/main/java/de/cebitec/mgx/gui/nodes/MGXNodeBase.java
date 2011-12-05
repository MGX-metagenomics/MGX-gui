package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public abstract class MGXNodeBase extends AbstractNode {

    protected MGXMaster master;

    protected MGXNodeBase(Children children, Lookup lookup) {
        super(children, lookup);
    }

    public MGXMaster getMaster() {
        return master;
    }
}
