package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.AttributeTypeAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.dtoconversion.AttributeTypeDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;

/**
 *
 * @author sjaenick
 */
public class AttributeTypeAccess extends MasterHolder implements AttributeTypeAccessI {

    public AttributeTypeAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public AttributeTypeI create(String name, char valueType, char structure) throws MGXException {
        if (valueType != AttributeTypeI.VALUE_DISCRETE && valueType != AttributeTypeI.VALUE_NUMERIC) {
            throw new MGXException(String.valueOf(valueType) + " is not a valid value type");
        }
        if (structure != AttributeTypeI.STRUCTURE_BASIC && structure != AttributeTypeI.STRUCTURE_HIERARCHICAL) {
            throw new MGXException(String.valueOf(structure) + " is not a valid attribute structure");
        }

        AttributeTypeI attrType = new AttributeType(getMaster(), Identifiable.INVALID_IDENTIFIER, name, valueType, structure);
        try {
            AttributeTypeDTO dto = AttributeTypeDTOFactory.getInstance().toDTO(attrType);
            long objId = getDTOmaster().AttributeType().create(dto);
            attrType.setId(objId);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return attrType;
    }

    @Override
    public AttributeTypeI fetch(long id) throws MGXException {
        AttributeTypeDTO h = null;
        try {
            h = getDTOmaster().AttributeType().fetch(id);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return AttributeTypeDTOFactory.getInstance().toModel(getMaster(), h);
    }

    @Override
    public Iterator<AttributeTypeI> fetchall() throws MGXException {
        Iterator<AttributeTypeDTO> it;
        try {
            it = getDTOmaster().AttributeType().fetchall().getAttributeTypeList().iterator();
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<AttributeTypeDTO, AttributeTypeI>(it) {
            @Override
            public AttributeTypeI next() {
                AttributeTypeI attr = AttributeTypeDTOFactory.getInstance().toModel(getMaster(), iter.next());
                return attr;
            }
        };
    }

    @Override
    public Iterator<AttributeTypeI> byJob(JobI job) throws MGXException {
        Iterator<AttributeTypeDTO> it;
        try {
            it = getDTOmaster().AttributeType().byJob(job.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<AttributeTypeDTO, AttributeTypeI>(it) {
            @Override
            public AttributeTypeI next() {
                AttributeTypeI attr = AttributeTypeDTOFactory.getInstance().toModel(getMaster(), iter.next());
                return attr;
            }
        };
    }

    @Override
    public TaskI<AttributeTypeI> delete(AttributeTypeI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Iterator<AttributeTypeI> BySeqRun(long id) throws MGXException {
        try {
            Iterator<AttributeTypeDTO> it = getDTOmaster().AttributeType().bySeqRun(id);
            return new BaseIterator<AttributeTypeDTO, AttributeTypeI>(it) {
                @Override
                public AttributeTypeI next() {
                    AttributeTypeI attr = AttributeTypeDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return attr;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    public Iterator<AttributeTypeI> ByJob(Job job) throws MGXException {
        try {
            Iterator<AttributeTypeDTO> it = getDTOmaster().AttributeType().byJob(job.getId());
            return new BaseIterator<AttributeTypeDTO, AttributeTypeI>(it) {
                @Override
                public AttributeTypeI next() {
                    AttributeTypeI attr = AttributeTypeDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return attr;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

}
