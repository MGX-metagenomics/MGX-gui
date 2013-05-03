package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.JobAndAttributeTypes;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.datamodel.misc.Task.TaskType;
import de.cebitec.mgx.gui.dtoconversion.AttributeTypeDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.JobDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.SeqRunDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class SeqRunAccess extends AccessBase<SeqRun> {

    @Override
    public long create(SeqRun obj) {
        SeqRunDTO dto = SeqRunDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().SeqRun().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.setId(id);
        obj.setMaster(this.getMaster());
        return id;
    }

    @Override
    public SeqRun fetch(long id) {
        SeqRunDTO dto = null;
        try {
            dto = getDTOmaster().SeqRun().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        SeqRun ret = SeqRunDTOFactory.getInstance().toModel(dto);
        ret.setMaster(this.getMaster());
        return ret;
    }

    @Override
    public Iterator<SeqRun> fetchall() {
        try {
            Iterator<SeqRunDTO> fetchall = getDTOmaster().SeqRun().fetchall();
            return new BaseIterator<SeqRunDTO, SeqRun>(fetchall) {
                @Override
                public SeqRun next() {
                    SeqRun sr = SeqRunDTOFactory.getInstance().toModel(iter.next());
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
    public void update(SeqRun obj) {
        SeqRunDTO dto = SeqRunDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().SeqRun().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.firePropertyChange(ModelBase.OBJECT_MODIFIED, null, obj);
    }

    @Override
    public Task delete(SeqRun obj) {
        Task ret = null;
        try {
            UUID uuid = getDTOmaster().SeqRun().delete(obj.getId());
            ret = getMaster().Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    public Iterator<SeqRun> ByExtract(final long extract_id) {
        try {
            Iterator<SeqRunDTO> fetchall = getDTOmaster().SeqRun().ByExtract(extract_id);
            return new BaseIterator<SeqRunDTO, SeqRun>(fetchall) {
                @Override
                public SeqRun next() {
                    SeqRun sr = SeqRunDTOFactory.getInstance().toModel(iter.next());
                    sr.setMaster(getMaster());
                    return sr;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public Map<Job, List<AttributeType>> getJobsAndAttributeTypes(long run_id) {
        Map<Job, List<AttributeType>> ret = new HashMap<>();
        try {
            for (JobAndAttributeTypes jat : getDTOmaster().SeqRun().getJobsAndAttributeTypes(run_id)) {
                Job job = JobDTOFactory.getInstance().toModel(jat.getJob());
                job.setMaster(this.getMaster());

                List<AttributeType> all = new ArrayList<>();
                for (AttributeTypeDTO atDTO : jat.getAttributeTypes().getAttributeTypeList()) {
                    AttributeType aType = AttributeTypeDTOFactory.getInstance().toModel(atDTO);
                    aType.setMaster(this.getMaster());
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
