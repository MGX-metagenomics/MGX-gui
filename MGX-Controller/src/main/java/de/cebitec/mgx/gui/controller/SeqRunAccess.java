package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.SeqRunAccessI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.ModelBase;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.JobAndAttributeTypes;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.gui.dtoconversion.AttributeTypeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.JobDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SeqRunDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class SeqRunAccess extends AccessBase<SeqRunI> implements SeqRunAccessI {

    public SeqRunAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        super(master, dtomaster);
    }

    
    @Override
    public SeqRunI create(SeqRunI obj) {
        SeqRunDTO dto = SeqRunDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().SeqRun().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        return obj;
    }

    @Override
    public SeqRunI fetch(long id) {
        SeqRunDTO dto = null;
        try {
            dto = getDTOmaster().SeqRun().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        SeqRunI ret = SeqRunDTOFactory.getInstance().toModel(getMaster(), dto);
        return ret;
    }

    @Override
    public Iterator<SeqRunI> fetchall() {
        try {
            Iterator<SeqRunDTO> fetchall = getDTOmaster().SeqRun().fetchall();
            return new BaseIterator<SeqRunDTO, SeqRunI>(fetchall) {
                @Override
                public SeqRunI next() {
                    SeqRunI sr = SeqRunDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return sr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public void update(SeqRunI obj) {
        SeqRunDTO dto = SeqRunDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().SeqRun().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.modified();
    }

    @Override
    public TaskI delete(SeqRunI obj) {
        TaskI ret = null;
        try {
            UUID uuid = getDTOmaster().SeqRun().delete(obj.getId());
            ret = getMaster().Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    public Iterator<SeqRunI> ByExtract(final long extract_id) {
        try {
            Iterator<SeqRunDTO> fetchall = getDTOmaster().SeqRun().ByExtract(extract_id);
            return new BaseIterator<SeqRunDTO, SeqRunI>(fetchall) {
                @Override
                public SeqRunI next() {
                    SeqRunI sr = SeqRunDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return sr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public Map<JobI, Set<AttributeTypeI>> getJobsAndAttributeTypes(SeqRunI run) {
        Map<JobI, Set<AttributeTypeI>> ret = new HashMap<>();
        try {
            for (JobAndAttributeTypes jat : getDTOmaster().SeqRun().getJobsAndAttributeTypes(run.getId())) {
                JobI job = JobDTOFactory.getInstance().toModel(getMaster(), jat.getJob());
                job.setSeqrun(run);

                Set<AttributeTypeI> all = new HashSet<>();
                for (AttributeTypeDTO atDTO : jat.getAttributeTypes().getAttributeTypeList()) {
                    AttributeTypeI aType = AttributeTypeDTOFactory.getInstance().toModel(getMaster(), atDTO);
                    all.add(aType);
                }

                ret.put(job, all);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        return ret;
    }
}
