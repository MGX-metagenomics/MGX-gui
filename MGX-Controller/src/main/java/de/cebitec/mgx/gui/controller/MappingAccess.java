package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.MappingAccessI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
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
import org.openide.util.Exceptions;

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
    public long create(MappingI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public MappingI fetch(long id) {
        MappingDTO dto = null;
        try {
            dto = dtomaster.Mapping().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        MappingI ret = MappingDTOFactory.getInstance().toModel(master, dto);
        return ret;
    }

    @Override
    public Iterator<MappingI> fetchall() {
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
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public Iterator<MappingI> BySeqRun(long runid) {
        try {
            Iterator<MappingDTO> fetchall = dtomaster.Mapping().BySeqRun(runid);
            return new BaseIterator<MappingDTO, MappingI>(fetchall) {
                @Override
                public MappingI next() {
                    MappingI sr = MappingDTOFactory.getInstance().toModel(master, iter.next());
                    return sr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public Iterator<MappingI> ByReference(long refId) {
        try {
            Iterator<MappingDTO> fetchall = dtomaster.Mapping().ByReference(refId);
            return new BaseIterator<MappingDTO, MappingI>(fetchall) {
                @Override
                public MappingI next() {
                    MappingI sr = MappingDTOFactory.getInstance().toModel(master, iter.next());
                    return sr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public void update(MappingI obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskI delete(MappingI obj) {
        TaskI ret = null;
        try {
            UUID uuid = dtomaster.Mapping().delete(obj.getId());
            ret = master.Task().get(obj, uuid, Task.TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    @Override
    public UUID openMapping(long id) {
        try {
            return dtomaster.Mapping().openMapping(id);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public void closeMapping(UUID uuid) {
        try {
            dtomaster.Mapping().closeMapping(uuid);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public long getMaxCoverage(UUID uuid) {
        try {
            return dtomaster.Mapping().getMaxCoverage(uuid);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return -1;
    }

    @Override
    public Iterator<MappedSequenceI> byReferenceInterval(UUID uuid, int from, int to) {
        try {
            Iterator<MappedSequenceDTO> mapped = dtomaster.Mapping().byReferenceInterval(uuid, from, to);
            return new BaseIterator<MappedSequenceDTO, MappedSequenceI>(mapped) {
                @Override
                public MappedSequenceI next() {
                    MappedSequenceI ms = MappedSequenceDTOFactory.getInstance().toModel(master, iter.next());
                    return ms;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;

    }
}
