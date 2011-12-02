package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public abstract class MGXNodeBase<T> extends AbstractNode {

    private MGXMaster master;

    public MGXNodeBase(Children children, Lookup lookup) {
        super(children, lookup);
    }

    public void setMaster(MGXMaster m) {
        master = m;
    }

    public MGXMaster getMaster() {
        return master;
    }
}
