package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.nodes.ReferenceNode;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author sj
 */
public class ReferenceNodeFactory extends ChildFactory<MGXReferenceI> implements NodeListener {

    private final MGXMasterI master;
    //
    private boolean refreshing = false;

    public ReferenceNodeFactory(MGXMasterI master) {
        this.master = master;
    }

    @Override
    protected boolean createKeys(List<MGXReferenceI> toPopulate) {
        Iterator<MGXReferenceI> iter = master.Reference().fetchall();
        while (iter.hasNext()) {
            toPopulate.add(iter.next());
        }
        Collections.sort(toPopulate);
        return true;
    }

    @Override
    protected Node createNodeForKey(MGXReferenceI ref) {
        Node node = new ReferenceNode(master, ref);
        node.addNodeListener(this);
        return node;
    }

    public void refreshChildren() {
        if (!refreshing) {
            refreshing = true;
            refresh(true);
            refreshing = false;
        }
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        //refresh(true);
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        //refresh(true);
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
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
    public void propertyChange(PropertyChangeEvent evt) {
        //refresh(true);
    }
}
