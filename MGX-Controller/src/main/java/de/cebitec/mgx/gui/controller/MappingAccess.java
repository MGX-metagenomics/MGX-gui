package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.MappedSequenceDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.MappingDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class MappingAccess extends AccessBase<Mapping> {

    @Override
    public long create(Mapping obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Mapping fetch(long id) {
        MappingDTO dto = null;
        try {
            dto = getDTOmaster().Mapping().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        Mapping ret = MappingDTOFactory.getInstance().toModel(dto);
        ret.setMaster(this.getMaster());
        return ret;
    }

    @Override
    public Iterator<Mapping> fetchall() {
        try {
            Iterator<MappingDTO> fetchall = getDTOmaster().Mapping().fetchall();
            return new BaseIterator<MappingDTO, Mapping>(fetchall) {
                @Override
                public Mapping next() {
                    Mapping sr = MappingDTOFactory.getInstance().toModel(iter.next());
                    sr.setMaster(getMaster());
                    return sr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public Iterator<Mapping> BySeqRun(long runid) {
        try {
            Iterator<MappingDTO> fetchall = getDTOmaster().Mapping().BySeqRun(runid);
            return new BaseIterator<MappingDTO, Mapping>(fetchall) {
                @Override
                public Mapping next() {
                    Mapping sr = MappingDTOFactory.getInstance().toModel(iter.next());
                    sr.setMaster(getMaster());
                    return sr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public Iterator<Mapping> ByReference(long refId) {
        try {
            Iterator<MappingDTO> fetchall = getDTOmaster().Mapping().ByReference(refId);
            return new BaseIterator<MappingDTO, Mapping>(fetchall) {
                @Override
                public Mapping next() {
                    Mapping sr = MappingDTOFactory.getInstance().toModel(iter.next());
                    sr.setMaster(getMaster());
                    return sr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public void update(Mapping obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Task delete(Mapping obj) {
        Task ret = null;
        try {
            UUID uuid = getDTOmaster().Mapping().delete(obj.getId());
            ret = getMaster().Task().get(obj, uuid, Task.TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    public Iterator<MappedSequence> byReferenceInterval(UUID uuid, int from, int to) throws MGXServerException, MGXClientException {
        try {
            Iterator<MappedSequenceDTO> mapped = getDTOmaster().Mapping().byReferenceInterval(uuid, from, to);
            return new BaseIterator<MappedSequenceDTO, MappedSequence>(mapped) {
                @Override
                public MappedSequence next() {
                    MappedSequence ms = MappedSequenceDTOFactory.getInstance().toModel(iter.next());
                    return ms;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;

    }
}
