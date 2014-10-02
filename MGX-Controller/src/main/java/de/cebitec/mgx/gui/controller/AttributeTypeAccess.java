package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
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
public class AttributeTypeAccess extends AccessBase<AttributeTypeI> {

    public AttributeTypeAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        super(master, dtomaster);
    }

    @Override
    public AttributeTypeI create(AttributeTypeI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public AttributeTypeI fetch(long id) {
        AttributeTypeDTO h = null;
        try {
            h = getDTOmaster().AttributeType().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return AttributeTypeDTOFactory.getInstance().toModel(getMaster(), h);
    }

    @Override
    public Iterator<AttributeTypeI> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void update(AttributeTypeI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Task delete(AttributeTypeI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Iterator<AttributeTypeI> BySeqRun(long id) throws MGXException {
        try {
            Iterator<AttributeTypeDTO> it = getDTOmaster().AttributeType().BySeqRun(id);
            return new BaseIterator<AttributeTypeDTO, AttributeTypeI>(it) {
                @Override
                public AttributeTypeI next() {
                    AttributeTypeI attr = AttributeTypeDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return attr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    public Iterator<AttributeTypeI> ByJob(Job job) throws MGXException {
        try {
            Iterator<AttributeTypeDTO> it = getDTOmaster().AttributeType().ByJob(job.getId());
            return new BaseIterator<AttributeTypeDTO, AttributeTypeI>(it) {
                @Override
                public AttributeTypeI next() {
                    AttributeTypeI attr = AttributeTypeDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return attr;
                }
            };
        } catch (MGXServerException ex) {
            throw new MGXException(ex);
        }
    }
}
