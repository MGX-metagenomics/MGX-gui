package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.openide.nodes.*;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class VisualizationGroupNodeFactory extends ChildFactory<SeqRun> implements NodeListener {
    
    private VisualizationGroup vg;
    
    public VisualizationGroupNodeFactory(VisualizationGroup vg) {
        this.vg = vg;
    }

    @Override
    protected boolean createKeys(List<SeqRun> toPopulate) {
        toPopulate.addAll(vg.getSeqRuns());
        return true;
    }

    @Override
    protected Node createNodeForKey(SeqRun key) {
        Node node = new AbstractNode(Children.LEAF, Lookups.singleton(key));
        node.addNodeListener(this);
        
        MGXMaster m = (MGXMaster)key.getMaster();
        node.setDisplayName(m.getProject() + " " + key.getSequencingTechnology());
        return node;
    }

    public void refreshChildren() {
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
}
