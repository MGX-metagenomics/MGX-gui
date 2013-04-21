package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.nodes.DNAExtractNode;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.*;

/**
 *
 * @author sj
 */
public class DNAExtractNodeFactory extends ChildFactory<DNAExtract> implements NodeListener {

    private MGXMaster master;
    private long sample_id;

    public DNAExtractNodeFactory(MGXMaster master, Sample s) {
        this.master = master;
        this.sample_id = s.getId();
    }

    @Override
    protected boolean createKeys(List<DNAExtract> toPopulate) {
        Iterator<DNAExtract> iter = master.DNAExtract().BySample(sample_id);
        while (iter.hasNext()) {
            toPopulate.add(iter.next());
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
