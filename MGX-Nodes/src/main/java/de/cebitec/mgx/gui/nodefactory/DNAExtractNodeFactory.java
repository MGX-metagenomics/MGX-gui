package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.nodes.DNAExtractNode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class DNAExtractNodeFactory extends MGXNodeFactoryBase<DNAExtractI> {

    private final SampleI sample;

    public DNAExtractNodeFactory(SampleI s) {
        super(s.getMaster());
        this.sample = s;
    }

    @Override
    protected boolean createKeys(List<DNAExtractI> toPopulate) {
        Iterator<DNAExtractI> iter = null;
        try {
            iter = getMaster().DNAExtract().BySample(sample);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        while (iter != null && iter.hasNext()) {
            if (Thread.interrupted()) {
                getMaster().log(Level.INFO, "interrupted in NF");
                return true;
            }
            toPopulate.add(iter.next());
        }
        Collections.sort(toPopulate);
        return true;
    }

    @Override
    protected Node createNodeForKey(DNAExtractI key) {
        DNAExtractNode node = new DNAExtractNode(key);
        node.addNodeListener(this);
        return node;
    }

//    public void refreshChildren() {
//        refresh(true);
//    }
//
//    @Override
//    public void childrenAdded(NodeMemberEvent ev) {
//        refresh(true);
//    }
//
//    @Override
//    public void childrenRemoved(NodeMemberEvent ev) {
//        refresh(true);
//    }
//
//    @Override
//    public void childrenReordered(NodeReorderEvent ev) {
//    }
//
//    @Override
//    public void nodeDestroyed(NodeEvent ev) {
//        refresh(true);
//    }
//
//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        //refresh(true);
//    }
}
