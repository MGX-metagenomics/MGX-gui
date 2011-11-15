package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.HabitatDTO;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class HabitatNodeFactory extends ChildFactory<HabitatDTO> {

    private MGXMaster master;

    HabitatNodeFactory(MGXMaster m) {
        this.master = m;
    }

    @Override
    protected boolean createKeys(List<HabitatDTO> toPopulate) {
        try {
            toPopulate.addAll(master.Habitat().fetchall());
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(HabitatDTO key) {
        AbstractNode habitat = new AbstractNode(Children.create(new SampleNodeFactory(master, key), true));
        habitat.setDisplayName(key.getName());
        return habitat;
    }
}
