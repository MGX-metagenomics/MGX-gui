package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.nodes.HabitatNode;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.openide.nodes.*;

/**
 *
 * @author sj
 */
public class HabitatNodeFactory extends ChildFactory<Habitat> implements NodeListener {

    private MGXMaster master;

    public HabitatNodeFactory(MGXMaster m) {
        this.master = m;
    }

    @Override
    protected boolean createKeys(List<Habitat> toPopulate) {
        toPopulate.addAll(master.Habitat().fetchall());
        return true;
    }

    @Override
    protected Node createNodeForKey(Habitat key) {
        HabitatNode node = new HabitatNode(master, key);
        node.addNodeListener(this);
        //node.addPropertyChangeListener(this);
        return node;
    }

    public void refreshChildren() {
        System.err.println("HNF refreshing");
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
        System.err.println("HNF got nodeDestroyed");
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.err.println("HNF got PCE "+evt.getPropertyName());
//        if (!Node.PROP_PARENT_NODE.equals(evt.getPropertyName())) {
//        refresh(true); }
    }
}
