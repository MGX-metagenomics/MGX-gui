package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.RegionDTO;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.ReferenceDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.RegionDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author belmann
 */
public class ReferenceAccess extends AccessBase<Reference> {

    @Override
    public long create(Reference obj) {
        dto.ReferenceDTO dto = ReferenceDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Reference().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        obj.setMaster(this.getMaster());
        return id;
    }

    @Override
    public Reference fetch(long id) {
        dto.ReferenceDTO dto = null;
        try {
            dto = getDTOmaster().Reference().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        Reference ret = ReferenceDTOFactory.getInstance().toModel(dto);
        ret.setMaster(this.getMaster());
        return ret;
    }

    @Override
    public Iterator<Reference> fetchall() {
        try {

            return new Iterator<Reference>() {
                final Iterator<ReferenceDTO> iter = getDTOmaster().Reference().fetchall();

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                public Reference next() {
                    Reference ret = ReferenceDTOFactory.getInstance().toModel(iter.next());
                    ret.setMaster(getMaster());
                    return ret;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported.");
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public void update(Reference obj) {
        ReferenceDTO dto = ReferenceDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Reference().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.firePropertyChange(ModelBase.OBJECT_MODIFIED, null, obj);
    }

    public Iterator<Region> byReferenceInterval(Long id, int from, int to) {
        Iterator<RegionDTO> fetchall;
        try {
            fetchall = getDTOmaster().Reference().byReferenceInterval(id, from, to);
            return new BaseIterator<RegionDTO, Region>(fetchall) {
                @Override
                public Region next() {
                    Region s = RegionDTOFactory.getInstance().toModel(iter.next());
                    s.setMaster(getMaster());
                    return s;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public Iterator<Reference> listGlobalReferences() {
        Iterator<ReferenceDTO> iter = null;
        try {
            iter = getDTOmaster().Reference().listGlobalReferences();
            return new BaseIterator<dto.ReferenceDTO, Reference>(iter) {
                @Override
                public Reference next() {
                    Reference reference = ReferenceDTOFactory.getInstance().toModel(iter.next());
                    // FIXME cannot set master
                    return reference;
                }
            };
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public long installGlobalReference(long id) throws MGXServerException {
        assert id != Identifiable.INVALID_IDENTIFIER;
        return getDTOmaster().Reference().installGlobalReference(id);
    }

    @Override
    public Task delete(Reference obj) {
        Task ret = null;
        try {
            UUID uuid = getDTOmaster().Reference().delete(obj.getId());
            ret = getMaster().Task().get(obj, uuid, Task.TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }
}
