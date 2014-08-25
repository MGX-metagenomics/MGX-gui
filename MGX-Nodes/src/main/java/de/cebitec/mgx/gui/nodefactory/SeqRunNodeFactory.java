package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
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
public class SeqRunNodeFactory extends ChildFactory<SeqRunI> implements NodeListener {

    private final MGXMasterI master;
    private final long extract_id;

    public SeqRunNodeFactory(MGXMasterI master, DNAExtractI key) {
        this.master = master;
        extract_id = key.getId();
    }

    @Override
    protected boolean createKeys(List<SeqRunI> toPopulate) {
        try {
            Iterator<SeqRunI> ByExtract = master.SeqRun().ByExtract(extract_id);
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
        SeqRunNode node = new SeqRunNode(master, key, Children.LEAF);
        node.addNodeListener(this);
        return node;
    }

    public void refreshChildren() {
        refresh(true);
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
