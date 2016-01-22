/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.nodes.FilterNode;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class SeqRunFilterNode extends FilterNode implements NodeListener {
    
    private final SeqRunNode n;
    private final VisualizationGroupI vGroup;

    public SeqRunFilterNode(SeqRunNode node, VisualizationGroupI vGroup) {
        super(node, Children.LEAF, Lookups.fixed(node, node.getContent(), vGroup));
        disableDelegation(DELEGATE_SET_DISPLAY_NAME + DELEGATE_GET_ACTIONS);
        n = node;
        this.vGroup = vGroup;
        node.addNodeListener(this);
    }

    @Override
    public void destroy() throws IOException {
        vGroup.removeSeqRun(n.getContent());
        fireNodeDestroyed();
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public boolean canCut() {
        return true;
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public String getDisplayName() {
        MGXMasterI m = n.getContent().getMaster();
        return n.getContent().getName() + " (" + m.getProject() + ")";
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{SystemAction.get(DeleteAction.class)};
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        fireNodeDestroyed();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }

    
}
