package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.nodes.HabitatNode;
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
public class HabitatNodeFactory extends ChildFactory<Habitat> implements NodeListener {

    private MGXMaster master;

    HabitatNodeFactory(MGXMaster m) {
        this.master = m;
    }

    @Override
    protected boolean createKeys(List<Habitat> toPopulate) {
        try {
            for (HabitatDTO dto : master.Habitat().fetchall()) {
                toPopulate.add(new Habitat(master, dto));
            }
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Habitat key) {
        HabitatNode node = new HabitatNode(Children.create(new SampleNodeFactory(master, key.getDTO().getId()), true), Lookups.singleton(key));
        node.setDisplayName(key.getName());
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
