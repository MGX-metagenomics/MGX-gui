package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.nodes.ReferenceNode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.nodes.NodeListener;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class ReferenceNodeFactory extends MGXNodeFactoryBase<MGXReferenceI> implements NodeListener {

    //
   // private boolean refreshing = false;

    public ReferenceNodeFactory(MGXMasterI master) {
       super(master);
    }

    @Override
    protected boolean createKeys(List<MGXReferenceI> toPopulate) {
        try {
            Iterator<MGXReferenceI> iter = getMaster().Reference().fetchall();
            while (iter.hasNext()) {
                toPopulate.add(iter.next());
            }
            Collections.sort(toPopulate);
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    @Override
    protected Node createNodeForKey(MGXReferenceI ref) {
        Node node = new ReferenceNode(ref);
        node.addNodeListener(this);
        return node;
    }

//    public void refreshChildren() {
//        if (!refreshing) {
//            refreshing = true;
//            refresh(true);
//            refreshing = false;
//        }
//    }

//    @Override
//    public void childrenAdded(NodeMemberEvent ev) {
//        //refresh(true);
//    }
//
//    @Override
//    public void childrenRemoved(NodeMemberEvent ev) {
//        //refresh(true);
//    }
//
//    @Override
//    public void childrenReordered(NodeReorderEvent ev) {
//    }
//
//    @Override
//    public void nodeDestroyed(NodeEvent ev) {
//        // this is ugly, and unnecessary everywhere else. however, here
//        // it triggers a stack overflow otherwise: refresh() makes the
//        // childfactory remove (and re-add) all nodes, which triggers a 
//        // nodeDestroyed() call for each removed node.
//        //
//        // I have no idea....
//        if (!refreshing) {
//            refreshing = true;
//            refresh(true);
//            refreshing = false;
//        }
//    }
//
//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        //refresh(true);
//    }
}
