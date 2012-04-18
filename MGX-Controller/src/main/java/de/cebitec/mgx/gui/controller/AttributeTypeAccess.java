package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.dtoconversion.AttributeTypeDTOFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class AttributeTypeAccess extends AccessBase<AttributeType> {

    @Override
    public Long create(AttributeType obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public AttributeType fetch(Long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public List<AttributeType> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(AttributeType obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public List<AttributeType> BySeqRun(Long id) {
        System.err.println("fetch atypes ");
        List<AttributeType> all = new ArrayList<>();
        try {
            for (AttributeTypeDTO dto : getDTOmaster().AttributeType().BySeqRun(id)) {
                AttributeType aType = AttributeTypeDTOFactory.getInstance().toModel(dto);
                aType.setMaster(getMaster());
                all.add(aType);

                System.err.println("recv " + aType.getName());
            }
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(DNAExtractAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    public List<AttributeType> ByJob(Job job) {
        List<AttributeType> all = new ArrayList<>();
        MGXMaster master = getMaster();
        try {
            for (AttributeTypeDTO dto : getDTOmaster().AttributeType().ByJob(job.getId())) {
                AttributeType aType = AttributeTypeDTOFactory.getInstance().toModel(dto);
                aType.setMaster(master);
                all.add(aType);
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(DNAExtractAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }
}
