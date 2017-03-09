package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class SeqRunNodeFactory extends MGXNodeFactoryBase<DNAExtractI, SeqRunI> {


    public SeqRunNodeFactory(DNAExtractI key) {
        super(key);
    }

    @Override
    protected boolean addKeys(List<SeqRunI> toPopulate) {
        try {
            Iterator<SeqRunI> iter = getContent().getMaster().SeqRun().ByExtract(getContent());
            while (iter != null && iter.hasNext()) {
                if (Thread.interrupted()) {
                    getContent().getMaster().log(Level.INFO, "interrupted in NF");
                    return true;
                }
                toPopulate.add(iter.next());
            }

            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    @Override
    protected SeqRunNode createNodeFor(SeqRunI key) {
        return new SeqRunNode(key, Children.LEAF);
    }

}
