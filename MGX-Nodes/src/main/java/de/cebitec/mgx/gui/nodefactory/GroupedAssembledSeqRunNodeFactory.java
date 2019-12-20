package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.groups.AssemblyGroupI;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.gui.nodes.AssembledSeqRunFilterNode;
import de.cebitec.mgx.gui.nodes.AssembledSeqRunNode;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author sjaenick
 */
public class GroupedAssembledSeqRunNodeFactory extends Children.Keys<AssembledSeqRunI> implements NodeListener {

    private final AssemblyGroupI asmGroup;

    public GroupedAssembledSeqRunNodeFactory(AssemblyGroupI group) {
        super(false);
        this.asmGroup = group;
        asmGroup.addPropertyChangeListener(this);
    }

    public void addSeqRun(AssembledSeqRunI sr) {
        asmGroup.add(sr);
    }

    public void addSeqRuns(AssembledSeqRunI... newRuns) {
        asmGroup.add(newRuns);
    }

    @Override
    protected Node[] createNodes(AssembledSeqRunI sr) {
        FilterNode node = new AssembledSeqRunFilterNode(new AssembledSeqRunNode(sr, Children.LEAF), asmGroup);
        node.addNodeListener(this);
        return new Node[]{node};
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        List<AssembledSeqRunI> runs = new ArrayList<>();
        runs.addAll(asmGroup.getContent());
        Collections.sort(runs);
        setKeys(runs);
    }

    @Override
    protected void removeNotify() {
        super.removeNotify();
        setKeys(Collections.<AssembledSeqRunI>emptySet());
    }

    public final void refreshChildren() {
        List<AssembledSeqRunI> runs = new ArrayList<>();
        runs.addAll(asmGroup.getContent());
        Collections.sort(runs);
        setKeys(runs);
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
        if (evt.getSource() == asmGroup) {
            switch (evt.getPropertyName()) {
                case VisualizationGroupI.VISGROUP_HAS_DIST:
                case VisualizationGroupI.VISGROUP_RENAMED:
                case VisualizationGroupI.VISGROUP_DEACTIVATED:
                case VisualizationGroupI.VISGROUP_ACTIVATED:
                case AssemblyGroupI.ASMGROUP_RENAMED:
                case AssemblyGroupI.ASMGROUP_HAS_DIST:
                case AssemblyGroupI.ASMGROUP_ACTIVATED:
                case AssemblyGroupI.ASMGROUP_DEACTIVATED:
                    return;
                //case VisualizationGroupI.VISGROUP_CHANGED:
                case AssemblyGroupI.ASMGROUP_CHANGED:
                    refreshChildren();
                    return;
                case GroupI.OBJECT_DELETED:
                    if (asmGroup.equals(evt.getSource())) {
                        asmGroup.removePropertyChangeListener(this);
                    }
                    break;
                default:
                    System.err.println(getClass().getName() + " in GroupedAssembledSeqRunNodeFactory got PCE " + evt.toString());
                    refreshChildren();
            }
        } else {
            refresh();
        }
    }

}
