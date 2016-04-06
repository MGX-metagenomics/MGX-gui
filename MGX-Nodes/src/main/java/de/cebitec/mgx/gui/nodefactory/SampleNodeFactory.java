package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.nodes.SampleNode;
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
public class SampleNodeFactory extends MGXNodeFactoryBase<SampleI> {

    private final HabitatI habitat;

    public SampleNodeFactory(HabitatI h) {
        super(h.getMaster());
        this.habitat = h;
    }

    @Override
    protected boolean addKeys(List<SampleI> toPopulate) {
        try {
            Iterator<SampleI> iter = getMaster().Sample().ByHabitat(habitat);
            while (iter != null && iter.hasNext()) {
                if (Thread.interrupted()) {
                    getMaster().log(Level.INFO, "interrupted in NF");
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
    protected Node createNodeForKey(SampleI key) {
        SampleNode node = new SampleNode(key);
        node.addNodeListener(this);
        return node;
    }
}
