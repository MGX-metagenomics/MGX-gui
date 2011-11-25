package de.cebitec.mgx.gui.access;

import de.cebitec.mgx.gui.datamodel.Sample;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class SampleAccess extends AccessBase<Sample> {

    @Override
    public long create(Sample obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Sample fetch(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Sample> fetchall() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Sample obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterable<Sample> ByHabitat(long hab_id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    
}
