package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.nodes.HabitatNode;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

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
