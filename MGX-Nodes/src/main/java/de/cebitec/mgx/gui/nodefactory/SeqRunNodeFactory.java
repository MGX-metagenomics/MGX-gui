package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.*;

/**
 *
 * @author sj
 */
public class SeqRunNodeFactory extends ChildFactory<SeqRun> implements NodeListener {

    private MGXMaster master;
    private long extract_id;

    public SeqRunNodeFactory(MGXMaster master, DNAExtract key) {
        this.master = master;
        extract_id = key.getId();
    }

    @Override
    protected boolean createKeys(List<SeqRun> toPopulate) {
        Iterator<SeqRun> ByExtract = master.SeqRun().ByExtract(extract_id);
        while (ByExtract.hasNext()) {
            toPopulate.add(ByExtract.next());
        }

        Collections.sort(toPopulate);
        return true;
    }

    @Override
    protected Node createNodeForKey(SeqRun key) {
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
