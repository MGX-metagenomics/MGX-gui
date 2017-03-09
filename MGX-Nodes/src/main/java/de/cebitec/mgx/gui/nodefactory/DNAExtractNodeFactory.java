package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.nodes.DNAExtractNode;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class DNAExtractNodeFactory extends MGXNodeFactoryBase<SampleI, DNAExtractI> {

    public DNAExtractNodeFactory(SampleI s) {
        super(s);
    }

    @Override
    protected boolean addKeys(List<DNAExtractI> toPopulate) {
        Iterator<DNAExtractI> iter = null;
        try {
            iter = getContent().getMaster().DNAExtract().BySample(getContent());
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        while (iter != null && iter.hasNext()) {
            if (Thread.interrupted()) {
                getContent().getMaster().log(Level.INFO, "interrupted in NF");
                return true;
            }
            toPopulate.add(iter.next());
        }
        return true;
    }

    @Override
    protected DNAExtractNode createNodeFor(DNAExtractI key) {
        return new DNAExtractNode(key);
    }
}
