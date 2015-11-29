package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.nodes.VizGroupNode;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.gui.nodes.ReplicateNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
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
 */
public class ReplicateNodeFactory extends ChildFactory<ReplicateI> implements NodeListener {

    private final ReplicateGroupI group;
    //private final List<SeqRunNode> nodes = new ArrayList<>();

    public ReplicateNodeFactory(ReplicateGroupI group) {
        this.group = group;
        group.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.err.println("RGNF got PCE " + evt.toString());
                switch (evt.getPropertyName()) {
                    case ReplicateGroupI.REPLICATEGROUP_REPLICATE_ADDED:
                    case ReplicateGroupI.REPLICATEGROUP_REPLICATE_REMOVED:
                        refreshChildren();
                        return;
                    case VisualizationGroupI.VISGROUP_RENAMED:
                    case VisualizationGroupI.VISGROUP_DEACTIVATED:
                    case VisualizationGroupI.VISGROUP_ACTIVATED:
                        return;
                    case VisualizationGroupI.VISGROUP_CHANGED:
                        refreshChildren();
                        return;
                    default:
                        System.err.println("RGNF got unhandled PCE " + evt.toString());
                        refreshChildren();
                }
            }
        });
    }

    @Override
    protected boolean createKeys(List<ReplicateI> toPopulate) {
        toPopulate.addAll(group.getReplicates());
        Collections.sort(toPopulate);
        return true;
    }

    public void addReplicate(ReplicateI r) {
        group.add(r);
        refreshChildren();
    }

//    public void removeNode(SeqRunNode node) {
//        node.removeNodeListener(this);
//        //nodes.remove(node);
//        group.removeSeqRun(node.getContent());
//        refreshChildren();
//    }
    @Override
    protected Node createNodeForKey(ReplicateI replicate) {
        Node rNode = new ReplicateNode(replicate);
        rNode.addNodeListener(this);
        return rNode;
    }

    public final void refreshChildren() {
        refresh(true);
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        refresh(true);
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        refresh(true);
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //refresh(true);
    }

}
