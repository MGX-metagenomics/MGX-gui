package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.nodes.DNAExtractNode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class DNAExtractNodeFactory extends MGXNodeFactoryBase<DNAExtractI> {

    private final SampleI sample;

    public DNAExtractNodeFactory(SampleI s) {
        super(s.getMaster());
        this.sample = s;
    }

    @Override
    protected boolean addKeys(List<DNAExtractI> toPopulate) {
        Iterator<DNAExtractI> iter = null;
        try {
            iter = sample.getMaster().DNAExtract().BySample(sample);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        while (iter != null && iter.hasNext()) {
            if (Thread.interrupted()) {
                sample.getMaster().log(Level.INFO, "interrupted in NF");
                return true;
            }
            toPopulate.add(iter.next());
        }
        Collections.sort(toPopulate);
        return true;
    }

    @Override
    protected Node createNodeFor(DNAExtractI key) {
        return new DNAExtractNode(key);
    }
}
