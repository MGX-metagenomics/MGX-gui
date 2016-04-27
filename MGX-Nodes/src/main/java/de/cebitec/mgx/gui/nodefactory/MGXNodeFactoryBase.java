package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.openide.nodes.ChildFactory;
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
        if (master != null && master.isDeleted()) {
            toPopulate.clear();
            return true;
        } else {
            return addKeys(toPopulate);
        }
    }

    protected abstract boolean addKeys(List<T> toPopulate);

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
            if (!master.isDeleted()) {
                refreshing = true;
                refresh(true);
                refreshing = false;
            }
        }
    }

    @Override
    public final void childrenAdded(NodeMemberEvent ev) {
        refresh(true);
    }

    @Override
    public final void childrenRemoved(NodeMemberEvent ev) {
        refresh(true);
    }

    @Override
    public final void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public final void nodeDestroyed(NodeEvent ev) {
        refresh(true);
    }

    @Override
    public final void propertyChange(PropertyChangeEvent evt) {
        //System.err.println("MGXNFBase: " + evt.toString() + " in " + getClass().getName());
    }
}
