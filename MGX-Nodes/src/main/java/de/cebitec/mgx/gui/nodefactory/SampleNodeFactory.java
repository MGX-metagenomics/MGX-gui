package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.nodes.SampleNode;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class SampleNodeFactory extends ChildFactory<Sample> implements NodeListener {

    private MGXMaster master;
    private long hab_id;

    SampleNodeFactory(MGXMaster master, long hab_id) {
        this.master = master;
        this.hab_id = hab_id;
    }

    @Override
    protected boolean createKeys(List<Sample> toPopulate) {
        for (Sample s : master.Sample().ByHabitat(hab_id)) {
            toPopulate.add(s);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Sample key) {
        SampleNode node = new SampleNode(Children.create(new DNAExtractNodeFactory(master, key.getId()), true), Lookups.singleton(key));
        node.setMaster(master);
        node.setDisplayName(key.getMaterial());
        node.addNodeListener(this);
        return node;
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        this.refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
    }
}
