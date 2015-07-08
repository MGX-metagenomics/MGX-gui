package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
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
            refreshing = true;
            //System.err.println("refreshing on EDT? " + EventQueue.isDispatchThread());
            refresh(true);
            refreshing = false;
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
        // this is ugly, and unnecessary everywhere else. however, here
        // it triggers a stack overflow otherwise: refresh() makes the
        // childfactory remove (and re-add) all nodes, which triggers a 
        // nodeDestroyed() call for each removed node.
        //
        // I have no idea....
        if (!refreshing) {
            refreshing = true;
            refresh(true);
            refreshing = false;
        }
    }

    @Override
    public final void propertyChange(PropertyChangeEvent evt) { 
        //getMaster().log(Level.SEVERE, evt.toString() + " in " + getClass().getName());
        //refresh(true); 
    }
}
