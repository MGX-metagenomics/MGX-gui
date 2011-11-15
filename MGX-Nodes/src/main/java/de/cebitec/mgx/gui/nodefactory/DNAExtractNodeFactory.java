package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.SampleDTO;
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
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DNAExtractDTO key) {
        AbstractNode sample = new AbstractNode(Children.create(new SeqRunNodeFactory(master, key), true));
        sample.setDisplayName(key.getMethod());
        return sample;
    }
}
