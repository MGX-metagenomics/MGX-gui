package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.nodes.DNAExtractNode;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class DNAExtractNodeFactory extends ChildFactory<DNAExtractI> implements NodeListener {

    private final MGXMasterI master;
    private final SampleI sample;

    public DNAExtractNodeFactory(MGXMasterI master, SampleI s) {
        this.master = master;
        this.sample = s;
    }

    @Override
    protected boolean createKeys(List<DNAExtractI> toPopulate) {
        Iterator<DNAExtractI> iter = null;
        try {
            iter = master.DNAExtract().BySample(sample);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        while (iter != null && iter.hasNext()) {
            toPopulate.add(iter.next());
        }
        Collections.sort(toPopulate);
        return true;
    }

    @Override
    protected Node createNodeForKey(DNAExtractI key) {
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
