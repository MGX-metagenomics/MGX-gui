package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.nodes.HabitatNode;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.*;

/**
 *
 * @author sj
 */
public class HabitatNodeFactory extends ChildFactory<Habitat> implements NodeListener {

    private final MGXMaster master;

    public HabitatNodeFactory(MGXMaster m) {
        this.master = m;
    }

    @Override
    protected boolean createKeys(List<Habitat> toPopulate) {
        Iterator<Habitat> iter = master.Habitat().fetchall();
        while (iter.hasNext()) {
            toPopulate.add(iter.next());
        }
        Collections.sort(toPopulate);
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
        //System.err.println("HNF got PCE "+evt.getPropertyName());
//        if (!Node.PROP_PARENT_NODE.equals(evt.getPropertyName())) {
//        refresh(true); }
    }
}
