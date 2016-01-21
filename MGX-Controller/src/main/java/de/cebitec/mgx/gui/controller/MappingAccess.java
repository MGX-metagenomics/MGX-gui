package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.MappingAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXTimeoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.MappedSequenceDTO;
import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.MappedSequenceDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.MappingDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class MappingAccess extends MappingAccessI {

    private final MGXDTOMaster dtomaster;
    private final MGXMasterI master;

    public MappingAccess(MGXDTOMaster dtomaster, MGXMasterI master) {
        this.dtomaster = dtomaster;
        this.master = master;
    }

    @Override
    public MappingI create(MappingI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public MappingI fetch(long id) throws MGXException {
        MappingDTO dto = null;
        try {
            dto = dtomaster.Mapping().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
        MappingI ret = MappingDTOFactory.getInstance().toModel(master, dto);
        return ret;
    }

    @Override
    public Iterator<MappingI> fetchall() throws MGXException {
        try {
            Iterator<MappingDTO> fetchall = dtomaster.Mapping().fetchall();
            return new BaseIterator<MappingDTO, MappingI>(fetchall) {
                @Override
                public MappingI next() {
                    MappingI sr = MappingDTOFactory.getInstance().toModel(master, iter.next());
                    return sr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<MappingI> BySeqRun(SeqRunI run) throws MGXException {
        try {
            Iterator<MappingDTO> fetchall = dtomaster.Mapping().BySeqRun(run.getId());
            return new BaseIterator<MappingDTO, MappingI>(fetchall) {
                @Override
                public MappingI next() {
                    MappingI sr = MappingDTOFactory.getInstance().toModel(master, iter.next());
                    return sr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<MappingI> ByReference(MGXReferenceI ref) throws MGXException {
        try {
            Iterator<MappingDTO> fetchall = dtomaster.Mapping().ByReference(ref.getId());
            return new BaseIterator<MappingDTO, MappingI>(fetchall) {
                @Override
                public MappingI next() {
                    MappingI sr = MappingDTOFactory.getInstance().toModel(master, iter.next());
                    return sr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public void update(MappingI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskI<MappingI> delete(MappingI obj) throws MGXException {
        try {
            UUID uuid = dtomaster.Mapping().delete(obj.getId());
            return master.<MappingI>Task().get(obj, uuid, Task.TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public UUID openMapping(long id) throws MGXException {
        try {
            return dtomaster.Mapping().openMapping(id);
        } catch (MGXServerException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public void closeMapping(UUID uuid) throws MGXException {
        try {
            dtomaster.Mapping().closeMapping(uuid);
        } catch (MGXServerException ex) {
            if (ex.getMessage().contains("No mapping session for")) {
                // session already closed due to timeout
                throw new MGXTimeoutException(ex);
            }
            throw new MGXException(ex);
        }
    }

    @Override
    public long getMaxCoverage(UUID uuid) throws MGXException {
        try {
            return dtomaster.Mapping().getMaxCoverage(uuid);
        } catch (MGXServerException ex) {
            if (ex.getMessage().contains("No mapping session for")) {
                throw new MGXTimeoutException(ex);
            }
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<MappedSequenceI> byReferenceInterval(UUID uuid, int from, int to) throws MGXException {
        try {
            Iterator<MappedSequenceDTO> mapped = dtomaster.Mapping().byReferenceInterval(uuid, from, to);
            return new BaseIterator<MappedSequenceDTO, MappedSequenceI>(mapped) {
                @Override
                public MappedSequenceI next() {
                    MappedSequenceI ms = MappedSequenceDTOFactory.getInstance().toModel(iter.next());
                    return ms;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            if (ex.getMessage().contains("No mapping session for")) {
                throw new MGXTimeoutException(ex);
            }
            throw new MGXException(ex);
        }
    }
}
