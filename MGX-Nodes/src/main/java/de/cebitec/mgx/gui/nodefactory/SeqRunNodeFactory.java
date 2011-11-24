package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
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
public class SeqRunNodeFactory extends ChildFactory<SeqRun> implements NodeListener {

    private MGXMaster master;
    private long ex_id;

    SeqRunNodeFactory(MGXMaster master, DNAExtract key) {
        this.master = master;
        ex_id = key.getDTO().getId();
    }

    @Override
    protected boolean createKeys(List<SeqRun> toPopulate) {
        try {
            for (SeqRunDTO dto : master.SeqRun().ByExtract(ex_id)) {
                toPopulate.add(new SeqRun(master, dto));
            }
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(SeqRun key) {
        SeqRunNode node = new SeqRunNode(Children.LEAF, Lookups.singleton(key));
        node.setMaster(master);
        node.setDisplayName(key.getDTO().getSequencingMethod() + "run");
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
