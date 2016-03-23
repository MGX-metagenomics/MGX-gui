/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.gui.nodes.ReplicateGroupNode;
import de.cebitec.mgx.gui.nodes.VizGroupNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
public class VisualizationGroupSet extends ChildFactory<ModelBaseI> implements NodeListener {

    private final Set<ModelBaseI> data = new LinkedHashSet<>();

    public VisualizationGroupSet(VGroupManagerI vgmgr) {

        // restore state
        data.addAll(vgmgr.getAllVisualizationGroups());
        data.addAll(vgmgr.getReplicateGroups());

        // listen for changes
        vgmgr.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case Node.PROP_DISPLAY_NAME:
                    case Node.PROP_NAME:
                        break;
                    case VGroupManagerI.VISGROUP_ADDED:
                        add((VisualizationGroupI) evt.getNewValue());
                        refresh(true);
                        break;

//                    case ModelBaseI.OBJECT_DELETED:
//                        remove((ModelBaseI) evt.getSource());
//                        refresh(true);
//                        break;
                    case VGroupManagerI.REPLGROUP_ADDED:
                        add((ReplicateGroupI) evt.getNewValue());
                        refresh(true);
                        break;
                }
            }
        });
    }

    private void add(ModelBaseI obj) {
        if (!data.contains(obj)) {
            data.add(obj);
            obj.addPropertyChangeListener(this);
        }
    }

    private void remove(ModelBaseI obj) {
        if (data.contains(obj)) {
            data.remove(obj);
            obj.removePropertyChangeListener(this);
        }
    }

    @Override
    protected boolean createKeys(List<ModelBaseI> toPopulate) {
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
        //refresh(true);
        switch (evt.getPropertyName()) {
            case Node.PROP_PARENT_NODE:
            case Node.PROP_DISPLAY_NAME:
            case Node.PROP_NAME:
                // ignore
                break;
            case VisualizationGroupI.VISGROUP_HAS_DIST:
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
                // ignore
                break;
            case ModelBaseI.OBJECT_DELETED:
                remove((ModelBaseI) evt.getSource());
                break;
            case ModelBaseI.OBJECT_MODIFIED:
                // ignore
                break;
            default:
                System.err.println("Unknown event " + evt + " in VisualizationGroupSet");
        }
    }

    @Override
    protected Node createNodeForKey(ModelBaseI key) {
        Node node = null;
        if (key instanceof VisualizationGroupI) {
            VisualizationGroupI vg = (VisualizationGroupI) key;
            node = new VizGroupNode(vg);
        } else if (key instanceof ReplicateGroupI) {
            ReplicateGroupI rg = (ReplicateGroupI) key;
            node = new ReplicateGroupNode(rg);
        } else {
            assert false;
        }
        if (node != null) {
            node.addNodeListener(this);
        }
        return node;
    }

}
