package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeCount;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.dtoconversion.AttributeDTOFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class AttributeAccess extends AccessBase<Attribute> {

    public Collection<Attribute> listTypes() throws MGXServerException {
        Collection<Attribute> ret = new HashSet<Attribute>();
        Collection<AttributeDTO> dtolist = getDTOmaster().Attribute().listTypes();
        for (AttributeDTO adto : dtolist) {
            Attribute attr = AttributeDTOFactory.getInstance().toModel(adto);
            ret.add(attr);
        }
        return ret;
    }

    public Collection<Attribute> listTypesByJob(Long jobId) throws MGXServerException {
        Collection<Attribute> ret = new HashSet<Attribute>();
        Collection<AttributeDTO> dtolist = getDTOmaster().Attribute().listTypesByJob(jobId);
        for (AttributeDTO adto : dtolist) {
            Attribute attr = AttributeDTOFactory.getInstance().toModel(adto);
            ret.add(attr);
        }
        return ret;
    }

    public Collection<Attribute> listTypesBySeqRun(Long seqrunId) throws MGXServerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<Attribute, Long> getDistribution(String attributeName, Long jobId, List<Long> seqrun_ids) {
        Map<Attribute, Long> res = new HashMap<Attribute, Long>();
        try {
            for (AttributeCount ac : getDTOmaster().Attribute().getDistribution(attributeName, jobId, seqrun_ids)) {
                AttributeDTO adto = ac.getAttribute();
                Attribute attr = AttributeDTOFactory.getInstance().toModel(adto);
                Long count = ac.getCount();
                res.put(attr, count);
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(AttributeAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    @Override
    public Long create(Attribute obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Attribute fetch(Long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public List<Attribute> fetchall() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Attribute obj) {
        return;
    }

    @Override
    public void delete(Long id) {
        return;
    }
}
