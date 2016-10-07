package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author sjaenick
 * @param <T>
 */
public abstract class MGXNodeFactoryBase<T> extends ChildFactory<T> implements NodeListener {

    protected volatile boolean refreshing = false;
    private final MGXMasterI master;

    public MGXNodeFactoryBase(MGXMasterI master) {
        this.master = master;
    }

    protected final MGXMasterI getMaster() {
        return master;
    }

    @Override
    protected final synchronized boolean createKeys(List<T> toPopulate) {
        if (!toPopulate.isEmpty()) {
            System.err.println("createKeys on non-empty list for " + getClass().getSimpleName());
        }
        if (master != null && master.isDeleted()) {
            toPopulate.clear();
            return true;
        } else {
            return addKeys(toPopulate);
        }
    }

    protected abstract boolean addKeys(List<T> toPopulate);

    @Override
    protected final Node createNodeForKey(T key) {
        Node node = createNodeFor(key);
        node.addNodeListener(this);
        return node;
    }

    protected abstract Node createNodeFor(T key);

    public final void refreshChildren() {

        if (EventQueue.isDispatchThread()) {
            NonEDT.invoke(new Runnable() {

                @Override
                public void run() {
                    refreshChildren();
                }
            });
            return;
        }
        if (!refreshing) {
            if (master == null || !master.isDeleted()) {
                refreshing = true;
                refresh(true);
                refreshing = false;
            }
        }
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        if (ev.getDelta().length == 1 && ev.getDelta()[0].getClass().getSimpleName().equals("WaitFilterNode")) {
            return;
        }
        boolean needRefresh = false;
        for (Node n : ev.getDelta()) {
            if (!"WaitFilterNode".equals(n.getClass().getSimpleName())) {
                needRefresh = true;
            }
        }
        if (needRefresh) {
            refreshChildren();
        }
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        if (ev.getDelta().length == 1 && ev.getDelta()[0].getClass().getSimpleName().equals("WaitFilterNode")) {
            return;
        }
        System.err.println("childrenRemoved for " + ev.getDelta().length + " nodes");
        for (Node n : ev.getDelta()) {
            System.err.println("  " + n.getDisplayName());
        }
        refreshChildren();
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        //System.err.println(getClass().getSimpleName() + " got nodeDestroyed for " + ev.getNode().getClass().getSimpleName());
        ev.getNode().removeNodeListener(this);
        refreshChildren();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Node.PROP_PARENT_NODE:
                //ignore
                break;
            default:
                System.err.println("MGXNodeFactoryBase: " + evt.toString() + " in " + getClass().getName());
                break;
        }
    }
}
