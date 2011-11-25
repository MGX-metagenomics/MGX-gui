package de.cebitec.mgx.gui.access;

import de.cebitec.mgx.gui.datamodel.DNAExtract;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class DNAExtractAccess extends AccessBase<DNAExtract> {

    @Override
    public long create(DNAExtract obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DNAExtract fetch(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DNAExtract> fetchall() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(DNAExtract obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterable<DNAExtract> BySample(long sample_id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
