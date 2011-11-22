package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.gui.nodes.DNAExtractNode;
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
public class DNAExtractNodeFactory extends ChildFactory<DNAExtractDTO> {

    private MGXMaster master;
    private long sample_id;

    DNAExtractNodeFactory(MGXMaster master, SampleDTO key) {
        this.master = master;
        sample_id = key.getId();
    }

    @Override
    protected boolean createKeys(List<DNAExtractDTO> toPopulate) {
        try {
            toPopulate.addAll(master.DNAExtract().BySample(sample_id));
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DNAExtractDTO key) {
        DNAExtractNode node = new DNAExtractNode(Children.create(new SeqRunNodeFactory(master, key), true), Lookups.singleton(key));
        node.setMaster(master);
        node.setDTO(key);
        node.setDisplayName(key.getProtocolName());
        return node;
    }
}
