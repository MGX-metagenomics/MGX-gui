/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.attributevisualization;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.gui.attributevisualization.view.NodeProviderI;
import de.cebitec.mgx.gui.attributevisualization.view.NodeSelectionEvent;
import de.cebitec.mgx.gui.attributevisualization.view.NodeSelectionListener;
import de.cebitec.mgx.gui.attributevisualization.view.NodeSelectionProvider;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.Serial;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import javax.swing.ToolTipManager;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public abstract class GroupFrameBase<T extends GroupI<U>, U> extends javax.swing.JInternalFrame implements ExplorerManager.Provider, NodeProviderI, NodeSelectionProvider, PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final transient ExplorerManager exmngr = new ExplorerManager();
    private final T group;
    private final Node groupNode;
    private final VGroupManagerI vgmgr;

    public GroupFrameBase(VGroupManagerI vgmgr, T group, Node groupNode) {
        super();
        this.group = group;
        this.groupNode = groupNode;
        this.vgmgr = vgmgr;
        //
        group.addPropertyChangeListener(this);
        //
        // needed to receive selectionChange events
        vgmgr.addPropertyChangeListener(this);
        ToolTipManager.sharedInstance().registerComponent(this);
        //
        exmngr.setRootContext(groupNode);
        try {
            exmngr.setSelectedNodes(new Node[]{groupNode});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
        exmngr.addPropertyChangeListener(this);
    }

//    private boolean isUnderRoot(Node node) {
//        while (node != null) {
//            if (node.equals(groupNode)) {
//                return true;
//            }
//            node = node.getParentNode();
//        }
//        return false;
//    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ExplorerManager.PROP_SELECTED_NODES:
                fireNodeSelectionEvent();
                break;
            case VisualizationGroupI.VISGROUP_CHANGED:
                repaint();
                return;
            case ModelBaseI.OBJECT_DELETED:
                if (evt.getOldValue().equals(getContent())) {
                    dispose();
                }
                break;
            case VGroupManagerI.VISGROUP_ADDED:
            case VGroupManagerI.REPLGROUP_ADDED:
            case VGroupManagerI.ASMGROUP_ADDED:
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
            case ExplorerManager.PROP_NODE_CHANGE:
                // ignore
                break;
            default:
                System.err.println("In " + getClass().getName() + " received unhandled event " + evt);
            //assert false;
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
        if (nsl != null && nodeListeners != null) {
            nodeListeners.remove(nsl);
        }
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
    public final synchronized void dispose() {
        if (!isDisposed) {
            isDisposed = true;
            try {
                exmngr.setSelectedNodes(new Node[]{});
            } catch (PropertyVetoException ex) {
            }
            nodeListeners.clear();
            vgmgr.removePropertyChangeListener(this);
            group.removePropertyChangeListener(this);
            if (group instanceof GroupI) {
                GroupI<?> vgrp = (GroupI) group;
                vgrp.close();
            }
            super.dispose();
        }
    }

    protected final static NumberFormat decFormat = NumberFormat.getInstance(Locale.US);

    @Override
    public void print(Graphics g) {
        // the Metal LaF decoration of the MetalInternalFrameTitlePane
        // causes an IllegalArgumentException in the SVG screenshot
        // export if the frame is selected; thus, disable temporarily
        // before printing and restore original value afterwards
        boolean selected = isSelected();
        isSelected = false;
        super.print(g);
        isSelected = selected;
    }

}
