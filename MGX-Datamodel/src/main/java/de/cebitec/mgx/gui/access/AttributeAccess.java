package de.cebitec.mgx.gui.access;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.datamodel.Attribute;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class AttributeAccess extends AccessBase<Attribute> {

    public Collection<Attribute> listTypes() throws MGXServerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<Attribute> listTypesByJob(Long jobId) throws MGXServerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<Attribute, Integer> getDistribution(String attributeName, Long jobId, List<Long> seqrun_ids) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long create(Attribute obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Attribute fetch(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Attribute> fetchall() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Attribute obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
