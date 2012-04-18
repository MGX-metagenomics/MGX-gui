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

    protected MGXMaster master;
    protected T content;

    protected MGXNodeBase(Children children, Lookup lookup, T data) {
        super(children, lookup);
        content = data;
    }

    public MGXMaster getMaster() {
        return master;
    }

    public T getContent() {
        return content;
    }
}
