package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.nodes.SeqRunNode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class SeqRunNodeFactory extends MGXNodeFactoryBase<SeqRunI> {

    private final DNAExtractI extract;

    public SeqRunNodeFactory(DNAExtractI key) {
        super(key.getMaster());
        extract = key;
    }

    @Override
    protected boolean addKeys(List<SeqRunI> toPopulate) {
        try {
            Iterator<SeqRunI> iter = extract.getMaster().SeqRun().ByExtract(extract);
            while (iter != null && iter.hasNext()) {
                if (Thread.interrupted()) {
                    extract.getMaster().log(Level.INFO, "interrupted in NF");
                    return true;
                }
                toPopulate.add(iter.next());
            }

            Collections.sort(toPopulate);
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    @Override
    protected Node createNodeFor(SeqRunI key) {
        return new SeqRunNode(key, Children.LEAF);
    }

}
