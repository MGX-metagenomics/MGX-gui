package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.nodes.ReferenceNode;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.*;

/**
 *
 * @author sj
 */
public class ReferenceNodeFactory extends ChildFactory<Reference> implements NodeListener {

    private MGXMaster master;
    //
    private boolean refreshing = false;

    public ReferenceNodeFactory(MGXMaster master) {
        this.master = master;
    }

    @Override
    protected boolean createKeys(List<Reference> toPopulate) {
        Iterator<Reference> iter = master.Reference().fetchall();
        while (iter.hasNext()) {
            toPopulate.add(iter.next());
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Reference ref) {
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
