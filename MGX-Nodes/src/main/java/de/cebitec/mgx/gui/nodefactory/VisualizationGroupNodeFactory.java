package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

    private final VisualizationGroup group;
    private final List<SeqRunNode> nodes = new ArrayList<>();

    public VisualizationGroupNodeFactory(VisualizationGroup group) {
        this.group = group;
        group.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //System.err.println("VGNF got event "+evt.getPropertyName());
                refreshChildren();
            }
        });
    }
    
    @Override
    protected boolean createKeys(List<SeqRunNode> toPopulate) {
        toPopulate.addAll(nodes);
        return true;
    }

    public void addNode(SeqRunNode node) { 
        node.addNodeListener(this);
        nodes.add(node);
        group.addSeqRun(node.getContent());
        refreshChildren();
    }
    
    public void removeNode(SeqRunNode node) { 
        node.removeNodeListener(this);
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

    private class DisplayNode extends FilterNode implements NodeListener {
        
        private SeqRunNode n;
        private VisualizationGroupNodeFactory nf;

        public DisplayNode(SeqRunNode node, VisualizationGroupNodeFactory nodef) {
            super(node, Children.LEAF, Lookups.singleton(node.getContent()));
            disableDelegation(DELEGATE_SET_DISPLAY_NAME + DELEGATE_GET_ACTIONS);
            n = node;
            nf = nodef; 
            node.addNodeListener(this);
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
            nf.removeNode(n);
            //fireNodeDestroyed();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        }

        private class RemoveAction extends AbstractAction {

            public RemoveAction() {
                putValue(NAME, "Remove");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                nf.removeNode(n);
                //fireNodeDestroyed();
            }
        }
    }
}
