package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.gui.nodes.HabitatNode;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class HabitatNodeFactory extends MGXNodeFactoryBase<HabitatI> {

    private final MGXMasterI master;

    public HabitatNodeFactory(MGXMasterI m) {
        this.master = m;
    }

    @Override
    protected boolean createKeys(List<HabitatI> toPopulate) {
        try {
            Iterator<HabitatI> iter = master.Habitat().fetchall();
            while (iter.hasNext()) {
                toPopulate.add(iter.next());
            }
            Collections.sort(toPopulate);
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    @Override
    protected Node createNodeForKey(HabitatI key) {
        HabitatNode node = new HabitatNode(master, key);
        node.addNodeListener(this);
        return node;
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
