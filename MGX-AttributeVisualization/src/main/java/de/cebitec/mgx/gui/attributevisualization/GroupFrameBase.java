/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.gui.attributevisualization.view.NodeProviderI;
import de.cebitec.mgx.gui.attributevisualization.view.NodeSelectionEvent;
import de.cebitec.mgx.gui.attributevisualization.view.NodeSelectionListener;
import de.cebitec.mgx.gui.attributevisualization.view.NodeSelectionProvider;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.ToolTipManager;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public abstract class GroupFrameBase<T extends ModelBaseI<T>> extends javax.swing.JInternalFrame implements ExplorerManager.Provider, NodeProviderI, NodeSelectionProvider, PropertyChangeListener {

    private final transient ExplorerManager exmngr = new ExplorerManager();
    private final T group;
    private final Node groupNode;

    public GroupFrameBase(T group, Node groupNode) {
        super();
        this.group = group;
        this.groupNode = groupNode;
        //
        group.addPropertyChangeListener(this);
        //
        // needed to receive selectionChange events
        VGroupManager.getInstance().addPropertyChangeListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
        //
        exmngr.setRootContext(groupNode);
        try {
            exmngr.setSelectedNodes(new Node[]{groupNode});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }

        exmngr.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ExplorerManager.PROP_SELECTED_NODES)) {
                    Node[] nodes = (Node[]) evt.getNewValue();
//                    System.err.println("sending new node selection:");
//                    for (Node n : nodes) {
//                        System.err.println("  " + n.getClass().getSimpleName() + " " + n.getName());
//                        if (!isUnderRoot(n)) {
//                            System.err.println("  NOT UNDER ROOT!");
//                        }
//                        if (n.getParentNode() == null) {
//                            System.err.println("  NO PARENT!");
//                        } else {
//                            System.err.println("    P: " + n.getParentNode().getName());
//                        }
//                        for (Object o : n.getLookup().lookupAll(Object.class)) {
//                            System.err.println("    L: " + o);
//                        }
//                    }
                    fireNodeSelectionEvent();
                }
            }
        });
    }

    private boolean isUnderRoot(Node node) {
        while (node != null) {
            if (node.equals(groupNode)) {
                return true;
            }
            node = node.getParentNode();
        }
        return false;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case VisualizationGroupI.VISGROUP_CHANGED:
                repaint();
                return;
            case ModelBaseI.OBJECT_DELETED:
                if (evt.getOldValue().equals(getContent())) {
                    dispose();
                    return;
                }
            case VGroupManagerI.VISGROUP_ADDED:
            case VGroupManagerI.REPLGROUP_ADDED:
            case VisualizationGroupI.VISGROUP_HAS_DIST:
                return;
            case VGroupManagerI.REPLICATEGROUP_SELECTION_CHANGED:
            case VGroupManagerI.VISGROUP_SELECTION_CHANGED:
                try {
                    setSelected(getContent() == evt.getNewValue());
                } catch (PropertyVetoException ex) {
                    Exceptions.printStackTrace(ex);
                }
                break;
            default:
                System.err.println("In " + getClass().getName() + " received unhandled event " + evt);
                assert false;
        }
    }

    @Override
    public final void setSelected(boolean selected) throws PropertyVetoException {
        super.setSelected(selected);
        if (selected) {
            fireNodeSelectionEvent();
        }
    }

    private Collection<NodeSelectionListener> nodeListeners = null;

    @Override
    public final void addNodeSelectionListener(NodeSelectionListener nsl) {
        if (nodeListeners == null) {
            nodeListeners = new ArrayList<>();
        }
        nodeListeners.add(nsl);
    }

    @Override
    public final void removeNodeSelectionListener(NodeSelectionListener nsl) {
        nodeListeners.remove(nsl);
    }

    private void fireNodeSelectionEvent() {
        if (nodeListeners != null && !nodeListeners.isEmpty()) {
            NodeSelectionEvent nev = new NodeSelectionEvent(this, exmngr.getSelectedNodes());
            for (NodeSelectionListener nsl : nodeListeners) {
                nsl.handleNodeSelection(nev);
            }
        }
    }

    @Override
    public final Node[] getSelectedNodes() {
        if (!isSelected) {
            return new Node[]{groupNode};
        }
        Node[] ret = exmngr.getSelectedNodes();
        return ret;
    }

    @Override
    public final Node getRootNode() {
        return groupNode;
    }

    @Override
    public final ExplorerManager getExplorerManager() {
        return exmngr;
    }

    public final T getContent() {
        return group;
    }

    private volatile boolean isDisposed = false;

    @Override
    public synchronized void dispose() {
        if (!isDisposed) {
            isDisposed = true;
            try {
                exmngr.setSelectedNodes(new Node[]{});
            } catch (PropertyVetoException ex) {
            }
            nodeListeners.clear();
            VGroupManager.getInstance().removePropertyChangeListener(this);
            group.removePropertyChangeListener(this);
            if (group instanceof VisualizationGroupI) {
                VisualizationGroupI vgrp = (VisualizationGroupI) group;
                vgrp.close();
            } else if (group instanceof ReplicateGroupI) {
                ReplicateGroupI rgrp = (ReplicateGroupI) group;
                rgrp.close();
            }
            super.dispose();
        }
    }

    protected final static DecimalFormat decFormat = new DecimalFormat(",###");

}
