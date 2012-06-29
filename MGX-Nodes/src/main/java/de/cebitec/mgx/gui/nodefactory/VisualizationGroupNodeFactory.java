package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.*;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class VisualizationGroupNodeFactory extends ChildFactory<SeqRunNode> implements NodeListener {

    private VisualizationGroup group;
    private List<SeqRunNode> nodes = new ArrayList<SeqRunNode>();

    public VisualizationGroupNodeFactory(VisualizationGroup group) {
        this.group = group;
    }
    
    @Override
    protected boolean createKeys(List<SeqRunNode> toPopulate) {
        toPopulate.addAll(nodes);
        return true;
    }

    public void addNode(SeqRunNode node) {
        nodes.add(node);
        group.addSeqRun(node.getContent());
        refreshChildren();
    }
    
    public void removeNode(SeqRunNode node) {
        nodes.remove(node);
        group.removeSeqRun(node.getContent());
        refreshChildren();
    }

    @Override
    protected Node createNodeForKey(SeqRunNode n) {
        FilterNode node = new DisplayNode(n, this);
        node.addNodeListener(this);
        return node;
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

    private class DisplayNode extends FilterNode {
        
        private SeqRunNode n;
        private VisualizationGroupNodeFactory nf;

        public DisplayNode(SeqRunNode node, VisualizationGroupNodeFactory nodef) {
            super(node, Children.LEAF, Lookups.singleton(node.getContent()));
            disableDelegation(DELEGATE_SET_DISPLAY_NAME + DELEGATE_GET_ACTIONS);
            n = node;
            nf = nodef;
            
        }

        @Override
        public String getDisplayName() {
            MGXMaster m = (MGXMaster) n.getContent().getMaster();
            return n.getContent().getName() + " (" + m.getProject() + ")";
        }
        
        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{new RemoveAction()};
        }

        private class RemoveAction extends AbstractAction {

            public RemoveAction() {
                putValue(NAME, "Remove");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                nf.removeNode(n);
                fireNodeDestroyed();
            }
        }
    }
}
