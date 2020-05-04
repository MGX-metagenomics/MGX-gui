package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.SampleAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.nodes.SampleNode;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class SampleNodeFactory extends MGXNodeFactoryBase<HabitatI, SampleI> {

//    private final HabitatI habitat;

    public SampleNodeFactory(HabitatI h) {
        super(h);
//        this.habitat = h;
    }

    @Override
    protected boolean addKeys(List<SampleI> toPopulate) {
        try {
            HabitatI content = getContent();
            MGXMasterI master = content.getMaster();
            SampleAccessI sac = master.Sample();
            Iterator<SampleI> iter = sac.ByHabitat(content);
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
    protected SampleNode createNodeFor(SampleI key) {
        return new SampleNode(key);
    }
}
