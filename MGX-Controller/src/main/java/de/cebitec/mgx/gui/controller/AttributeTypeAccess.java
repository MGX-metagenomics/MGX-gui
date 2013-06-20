package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.AttributeTypeDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class AttributeTypeAccess extends AccessBase<AttributeType> {

    @Override
    public long create(AttributeType obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public AttributeType fetch(long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Iterator<AttributeType> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(AttributeType obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Task delete(AttributeType obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Iterator<AttributeType> BySeqRun(long id) {
        try {
            Iterator<AttributeTypeDTO> it = getDTOmaster().AttributeType().BySeqRun(id);
            return new BaseIterator<AttributeTypeDTO, AttributeType>(it) {
                @Override
                public AttributeType next() {
                    AttributeType attr = AttributeTypeDTOFactory.getInstance().toModel(iter.next());
                    attr.setMaster(getMaster());
                    return attr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public Iterator<AttributeType> ByJob(Job job) {
        try {
            Iterator<AttributeTypeDTO> it = getDTOmaster().AttributeType().ByJob(job.getId());
            return new BaseIterator<AttributeTypeDTO, AttributeType>(it) {
                @Override
                public AttributeType next() {
                    AttributeType attr = AttributeTypeDTOFactory.getInstance().toModel(iter.next());
                    attr.setMaster(getMaster());
                    return attr;
                }
            };
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
