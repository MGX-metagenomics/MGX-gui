package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class GroupedSeqRunNodeFactory extends ChildFactory<SeqRunI> implements NodeListener {

    private final VisualizationGroupI group;
    //private final List<SeqRunNode> nodes = new ArrayList<>();

    public GroupedSeqRunNodeFactory(VisualizationGroupI group) {
        this.group = group;
        group.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case VisualizationGroupI.VISGROUP_HAS_DIST:
                    case VisualizationGroupI.VISGROUP_RENAMED:
                    case VisualizationGroupI.VISGROUP_DEACTIVATED:
                    case VisualizationGroupI.VISGROUP_ACTIVATED:
                        return;
                    case VisualizationGroupI.VISGROUP_CHANGED:
                        refreshChildren();
                        return;
                    default:
                        System.err.println("VGNF got PCE " + evt.toString());
                        refreshChildren();
                }
            }
        });
    }

    @Override
    protected boolean createKeys(List<SeqRunI> toPopulate) {
        toPopulate.addAll(group.getSeqRuns());
        Collections.sort(toPopulate);
        return true;
    }

    public void addSeqRun(SeqRunI sr) {
        //node.addNodeListener(this);
        //nodes.add(node);
        group.addSeqRun(sr);
        refreshChildren();
    }

    public void addSeqRuns(Set<SeqRunI> newRuns) {
//        Set<SeqRunI> newRuns = new HashSet<>();
//        for (SeqRunI node : newNodes) {
//            //node.addNodeListener(this);
//            //nodes.add(node);
//            //newRuns.add(node.getContent());
//        }
        group.addSeqRuns(newRuns);
        refreshChildren();
    }

    public void removeNode(SeqRunNode node) {
        node.removeNodeListener(this);
        //nodes.remove(node);
        group.removeSeqRun(node.getContent());
        refreshChildren();
    }

    @Override
    protected Node createNodeForKey(SeqRunI sr) {
        SeqRunNode n = new SeqRunNode(sr, Children.LEAF);
        FilterNode node = new SeqRunFilterNode(n, this);
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

    private class SeqRunFilterNode extends FilterNode implements NodeListener {

        private final SeqRunNode n;
        private final GroupedSeqRunNodeFactory nf;

        public SeqRunFilterNode(SeqRunNode node, GroupedSeqRunNodeFactory nodef) {
            super(node, Children.LEAF, Lookups.singleton(node.getContent()));
            disableDelegation(DELEGATE_SET_DISPLAY_NAME + DELEGATE_GET_ACTIONS);
            n = node;
            nf = nodef;
            node.addNodeListener(this);
        }

        @Override
        public String getDisplayName() {
            MGXMasterI m = n.getContent().getMaster();
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
