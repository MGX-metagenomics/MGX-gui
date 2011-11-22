package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.gui.nodes.SampleNode;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class SampleNodeFactory extends ChildFactory<SampleDTO> {

    private MGXMaster master;
    private long hab_id;

    SampleNodeFactory(MGXMaster master, HabitatDTO key) {
        this.master = master;
        hab_id = key.getId();
    }

    @Override
    protected boolean createKeys(List<SampleDTO> toPopulate) {
        try {
            toPopulate.addAll(master.Sample().ByHabitat(hab_id));
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(SampleDTO key) {
        SampleNode node = new SampleNode(Children.create(new DNAExtractNodeFactory(master, key), true), Lookups.singleton(key));
        node.setMaster(master);
        node.setDTO(key);
        node.setDisplayName(key.getMaterial());
        return node;
    }
}
