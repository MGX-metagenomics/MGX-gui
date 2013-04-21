package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.nodes.SampleNode;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.*;

/**
 *
 * @author sj
 */
public class SampleNodeFactory extends ChildFactory<Sample> implements NodeListener {

    private MGXMaster master;
    private long habitat_id;

    public SampleNodeFactory(MGXMaster master, Habitat h) {
        this.master = master;
        this.habitat_id = h.getId();
    }

    @Override
    protected boolean createKeys(List<Sample> toPopulate) {
        Iterator<Sample> iter = master.Sample().ByHabitat(habitat_id);
        while (iter.hasNext()) {
            toPopulate.add(iter.next());
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(Sample key) {
        SampleNode node = new SampleNode(master, key);
        node.addNodeListener(this);
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
