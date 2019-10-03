/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.groups.AssemblyGroupI;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.gui.nodes.AssemblyGroupNode;
import de.cebitec.mgx.gui.nodes.ReplicateGroupNode;
import de.cebitec.mgx.gui.nodes.VizGroupNode;
import java.beans.PropertyChangeEvent;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
public class VisualizationGroupSet extends ChildFactory<GroupI> implements NodeListener {

    private final Set<GroupI> data = new LinkedHashSet<>();
    private final VGroupManagerI vgmgr;

    public VisualizationGroupSet(VGroupManagerI vgmgr) {
        this.vgmgr = vgmgr;

        // restore state
        data.addAll(vgmgr.getAllGroups());
        data.addAll(vgmgr.getReplicateGroups());

        // listen for changes
        vgmgr.addPropertyChangeListener(this);
    }

    private void add(GroupI obj) {
        if (!data.contains(obj)) {
            data.add(obj);
            obj.addPropertyChangeListener(this);
        }
    }

    private void remove(GroupI obj) {
        if (data.contains(obj)) {
            data.remove(obj);
            obj.removePropertyChangeListener(this);
        }
    }

    @Override
    protected boolean createKeys(List<GroupI> toPopulate) {
        toPopulate.addAll(data);
        return true;
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
        ev.getNode().removeNodeListener(this);
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == vgmgr) {
            switch (evt.getPropertyName()) {
                case Node.PROP_DISPLAY_NAME:
                case Node.PROP_NAME:
                    break;
                case VGroupManagerI.VISGROUP_ADDED:
                    add((VisualizationGroupI) evt.getNewValue());
                    refresh(true);
                    break;
                case VGroupManagerI.REPLGROUP_ADDED:
                    add((ReplicateGroupI) evt.getNewValue());
                    refresh(true);
                    break;
                case VGroupManagerI.ASMGROUP_ADDED:
                    add((AssemblyGroupI) evt.getNewValue());
                    refresh(true);
                    break;
            }
        } else {
            switch (evt.getPropertyName()) {
                case Node.PROP_PARENT_NODE:
                case Node.PROP_DISPLAY_NAME:
                case Node.PROP_NAME:
                    // ignore
                    break;
                case VisualizationGroupI.VISGROUP_HAS_DIST:
                case AssemblyGroupI.ASMGROUP_HAS_DIST:
                    // ignore
                    break;
                case VisualizationGroupI.VISGROUP_CHANGED:
                case VisualizationGroupI.VISGROUP_ACTIVATED:
                case VisualizationGroupI.VISGROUP_DEACTIVATED:
                case VisualizationGroupI.VISGROUP_RENAMED:
                case ReplicateGroupI.REPLICATEGROUP_ACTIVATED:
                case ReplicateGroupI.REPLICATEGROUP_DEACTIVATED:
                case ReplicateGroupI.REPLICATEGROUP_REPLICATE_ADDED:
                case ReplicateGroupI.REPLICATEGROUP_REPLICATE_REMOVED:
                case AssemblyGroupI.ASMGROUP_CHANGED:
                case AssemblyGroupI.ASMGROUP_ACTIVATED:
                case AssemblyGroupI.ASMGROUP_DEACTIVATED:
                case AssemblyGroupI.ASMGROUP_RENAMED:
                    // ignore
                    break;
                case GroupI.OBJECT_DELETED:
                    remove((GroupI) evt.getSource());
                    break;
                case GroupI.OBJECT_MODIFIED:
                    // ignore
                    break;
                default:
                    System.err.println("Unknown event " + evt + " in VisualizationGroupSet");
            }
        }
    }

    @Override
    protected Node createNodeForKey(GroupI key) {
        Node node = null;
        if (key instanceof VisualizationGroupI) {
            VisualizationGroupI vg = (VisualizationGroupI) key;
            node = new VizGroupNode(vg);
        } else if (key instanceof ReplicateGroupI) {
            ReplicateGroupI rg = (ReplicateGroupI) key;
            node = new ReplicateGroupNode(rg);
        } else if (key instanceof AssemblyGroupI) {
            AssemblyGroupI ag = (AssemblyGroupI) key;
            node = new AssemblyGroupNode(ag);
        } else {
            assert false;
        }
        if (node != null) {
            node.addNodeListener(this);
        }
        return node;
    }

}
