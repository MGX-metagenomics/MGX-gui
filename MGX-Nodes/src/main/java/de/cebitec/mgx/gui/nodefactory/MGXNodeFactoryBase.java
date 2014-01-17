package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.swingutils.NonEDT;
import java.awt.EventQueue;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.NodeListener;

/**
 *
 * @author sjaenick
 * @param <T>
 */
public abstract class MGXNodeFactoryBase<T> extends ChildFactory<T> implements NodeListener {

    protected boolean refreshing = false;

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

   
}
