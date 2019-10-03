package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodes.SeqRunFilterNode;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author patrick
 */
public class SingleSeqRunNodeFactory extends Children.Keys<SeqRunI> implements NodeListener {

    private final VisualizationGroupI vGroup;

    public SingleSeqRunNodeFactory(VisualizationGroupI group) {
        super(false);
        this.vGroup = group;
        vGroup.addPropertyChangeListener(this);
    }

    public void addSeqRun(SeqRunI sr) {
        vGroup.add(sr);
    }

    public void addSeqRuns(SeqRunI... newRuns) {
        vGroup.add(newRuns);
    }

    @Override
    protected Node[] createNodes(SeqRunI sr) {
        FilterNode node = new SeqRunFilterNode(new SeqRunNode(sr, Children.LEAF), vGroup);
        node.addNodeListener(this);
        return new Node[]{node};
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        setKeys(vGroup.getContent());
    }

    @Override
    protected void removeNotify() {
        super.removeNotify();
        setKeys(Collections.<SeqRunI>emptySet());
    }

    public final void refreshChildren() {
        setKeys(vGroup.getContent());
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == vGroup) {
            switch (evt.getPropertyName()) {
                case VisualizationGroupI.VISGROUP_HAS_DIST:
                case VisualizationGroupI.VISGROUP_RENAMED:
                case VisualizationGroupI.VISGROUP_DEACTIVATED:
                case VisualizationGroupI.VISGROUP_ACTIVATED:
                    return;
                case VisualizationGroupI.VISGROUP_CHANGED:
                    refreshChildren();
                    return;
                case ModelBaseI.OBJECT_DELETED:
                    if (vGroup.equals(evt.getSource())) {
                        vGroup.removePropertyChangeListener(this);
                    }
                    break;
                default:
                    System.err.println(getClass().getName() + " in GroupedSeqRunNodeFactory got PCE " + evt.toString());
                    refreshChildren();
            }
        } else {
            refresh();
        }
    }

}
