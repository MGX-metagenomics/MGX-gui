package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class SeqRunNodeFactory extends MGXNodeFactoryBase<SeqRunI> {

    private final DNAExtractI extract;

    public SeqRunNodeFactory(DNAExtractI key) {
        super(key.getMaster());
        extract = key;
    }

    @Override
    protected boolean addKeys(List<SeqRunI> toPopulate) {
        try {
            Iterator<SeqRunI> ByExtract = getMaster().SeqRun().ByExtract(extract);
            while (ByExtract.hasNext()) {
                toPopulate.add(ByExtract.next());
            }
            
            Collections.sort(toPopulate);
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    @Override
    protected Node createNodeForKey(SeqRunI key) {
        SeqRunNode node = new SeqRunNode(key, Children.LEAF);
        node.addNodeListener(this);
        return node;
    }

//    public void refreshChildren() {
//        refresh(true);
//    }

//    @Override
//    public void childrenAdded(NodeMemberEvent ev) {
//    }
//
//    @Override
//    public void childrenRemoved(NodeMemberEvent ev) {
//    }
//
//    @Override
//    public void childrenReordered(NodeReorderEvent ev) {
//    }

//    @Override
//    public void nodeDestroyed(NodeEvent ev) {
//        this.refresh(true);
//    }

//    @Override
//    public void propertyChange(PropertyChangeEvent pce) {
//    }
}
