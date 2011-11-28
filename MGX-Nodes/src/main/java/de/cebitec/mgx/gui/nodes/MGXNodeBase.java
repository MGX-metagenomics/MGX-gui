package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.datamodel.MGXMasterI;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public abstract class MGXNodeBase<T> extends AbstractNode {

    private MGXMasterI master;

    public MGXNodeBase(Children children, Lookup lookup) {
        super(children, lookup);
    }

    public void setMaster(MGXMasterI m) {
        master = m;
    }

    public MGXMasterI getMaster() {
        return master;
    }
}
