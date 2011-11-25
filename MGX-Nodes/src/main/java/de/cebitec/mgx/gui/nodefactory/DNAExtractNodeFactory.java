package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.access.MGXMaster;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.nodes.DNAExtractNode;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class DNAExtractNodeFactory extends ChildFactory<DNAExtract> implements NodeListener {

    private MGXMaster master;
    private long sample_id;

    DNAExtractNodeFactory(MGXMaster master, long sample_id) {
        this.master = master;
        this.sample_id = sample_id;
    }

    @Override
    protected boolean createKeys(List<DNAExtract> toPopulate) {
        for (DNAExtract d : master.DNAExtract().BySample(sample_id)) {
            toPopulate.add(d);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DNAExtract key) {
        DNAExtractNode node = new DNAExtractNode(Children.create(new SeqRunNodeFactory(master, key), true), Lookups.singleton(key));
        node.setMaster(master);
        node.setDisplayName(key.getProtocolName());
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
