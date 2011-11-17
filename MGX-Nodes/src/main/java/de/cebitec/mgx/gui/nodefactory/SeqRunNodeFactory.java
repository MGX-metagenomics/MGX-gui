package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class SeqRunNodeFactory extends ChildFactory<SeqRunDTO> {

    private MGXMaster master;
    private long ex_id;

    SeqRunNodeFactory(MGXMaster master, DNAExtractDTO key) {
        this.master = master;
        ex_id = key.getId();
    }

    @Override
    protected boolean createKeys(List<SeqRunDTO> toPopulate) {
        try {
            toPopulate.addAll(master.SeqRun().ByExtract(ex_id));
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(SeqRunDTO key) {
        SeqRunNode seqrun = new SeqRunNode(Children.LEAF);
        seqrun.setDisplayName(key.getSequencingMethod() + "run");
        return seqrun;
    }
}
