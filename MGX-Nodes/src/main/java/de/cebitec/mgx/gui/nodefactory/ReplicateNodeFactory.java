package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.gui.nodes.ReplicateNode;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author sjaenick
 */
public class ReplicateNodeFactory extends Children.Keys<ReplicateI> implements NodeListener {

    private final ReplicateGroupI group;

    public ReplicateNodeFactory(final ReplicateGroupI group) {
        super(false);
        this.group = group;
        group.addPropertyChangeListener(this);
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        setKeys(group.getReplicates());
    }

    @Override
    protected void removeNotify() {
        super.removeNotify();
        setKeys(Collections.<ReplicateI>emptySet());
    }

    @Override
    protected Node[] createNodes(ReplicateI replicate) {
        ReplicateNode rNode = new ReplicateNode(replicate);
//        FilterNode fn = new ReplicateFilterNode(rNode, this);
//        fn.addNodeListener(this);
        rNode.addNodeListener(this);
        return new Node[]{rNode};
    }

    public final void refreshChildren() {
        setKeys(group.getReplicates());
        refresh();
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        refresh();
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        refresh();
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        refresh();
    }

//    public void addReplicate(ReplicateI r) {
//        group.add(r);
//        refreshChildren();
//    }
//
//    public void removeNode(ReplicateNode node) {
//        node.removeNodeListener(this);
//        //nodes.remove(node);
//        VisualizationGroupI vg = node.getContent();
//        assert vg instanceof ReplicateI;
//        group.remove((ReplicateI) vg);
//        refreshChildren();
//    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == group) {
            switch (evt.getPropertyName()) {
                case ReplicateGroupI.REPLICATEGROUP_REPLICATE_ADDED:
                case ReplicateGroupI.REPLICATEGROUP_REPLICATE_REMOVED:
                    if (evt.getSource().equals(group)) {
                        refreshChildren();
                    }
                    break;
                case ReplicateGroupI.REPLICATEGROUP_ACTIVATED:
                case ReplicateGroupI.REPLICATEGROUP_DEACTIVATED:
                    break;
                case VisualizationGroupI.VISGROUP_RENAMED:
                case VisualizationGroupI.VISGROUP_DEACTIVATED:
                case VisualizationGroupI.VISGROUP_ACTIVATED:
                case VisualizationGroupI.VISGROUP_HAS_DIST:
                    return;
                case VisualizationGroupI.VISGROUP_CHANGED:
                    refreshChildren();
                    return;
                case ModelBaseI.OBJECT_DELETED:
                    if (group.equals(evt.getSource())) {
                        group.removePropertyChangeListener(this);
                    }
                    break;
                case ModelBaseI.OBJECT_MODIFIED:
                    if (group.equals(evt.getSource())) {
                        refreshChildren();
                    }
                    break;
                default:
                    System.err.println("ReplicateNodeFactory got unhandled PCE " + evt.toString());
                    refreshChildren();
            }
        }
        //refresh(true);
    }

}
