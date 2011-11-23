package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.Sample;
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
public class DNAExtractNodeFactory extends ChildFactory<DNAExtract> {

    private MGXMaster master;
    private long sample_id;

    DNAExtractNodeFactory(MGXMaster master, Sample key) {
        this.master = master;
        sample_id = key.getDTO().getId();
    }

    @Override
    protected boolean createKeys(List<DNAExtract> toPopulate) {
        try {
            for (DNAExtractDTO dto : master.DNAExtract().BySample(sample_id)) {
                toPopulate.add(new DNAExtract(master, dto));
            }
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DNAExtract key) {
        DNAExtractNode node = new DNAExtractNode(Children.create(new SeqRunNodeFactory(master, key), true), Lookups.singleton(key));
        node.setMaster(master);
        node.setDisplayName(key.getDTO().getProtocolName());
        return node;
    }
}
