package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.Sample;
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
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class DNAExtractNodeFactory extends ChildFactory<DNAExtract> implements NodeListener {

    private MGXMaster master;
    private Sample sample;

    public DNAExtractNodeFactory(MGXMaster master, Sample s) {
        this.master = master;
        this.sample = s;
    }

    @Override
    protected boolean createKeys(List<DNAExtract> toPopulate) {
        for (DNAExtract d : master.DNAExtract().BySample(sample.getId())) {
            toPopulate.add(d);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DNAExtract key) {
        DNAExtractNode node = new DNAExtractNode(master, key);
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
