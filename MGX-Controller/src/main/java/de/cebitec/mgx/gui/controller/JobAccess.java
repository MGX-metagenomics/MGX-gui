package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.datamodel.misc.Task.TaskType;
import de.cebitec.mgx.gui.dtoconversion.JobDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class JobAccess extends AccessBase<Job> {

    public boolean verify(Job obj) throws MGXServerException {
        assert obj.getId() != Identifiable.INVALID_IDENTIFIER;
        boolean ret = getDTOmaster().Job().verify(obj.getId());
        obj.modified();
        return ret;
    }

    public boolean execute(Job obj) throws MGXServerException {
        assert obj.getId() != Identifiable.INVALID_IDENTIFIER;
        boolean ret = getDTOmaster().Job().execute(obj.getId());
        obj.modified();
        return ret;
    }

    public Task restart(Job job) {
        Task ret = null;
        try {
            UUID uuid = getDTOmaster().Job().restart(job.getId());
            ret = getMaster().Task().get(job, uuid, TaskType.MODIFY);
            job.modified();
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    public boolean cancel(Job obj) throws MGXServerException {
        assert obj.getId() != Identifiable.INVALID_IDENTIFIER;
        boolean ret = getDTOmaster().Job().cancel(obj.getId());
        obj.modified();
        return ret;
    }

    @Override
    public long create(Job obj) {
        assert obj.getTool().getId() != Identifiable.INVALID_IDENTIFIER;
        assert obj.getSeqrun().getId() != Identifiable.INVALID_IDENTIFIER;

        JobDTO dto = JobDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Job().create(dto);
            obj.setId(id);
            obj.modified();
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        obj.setMaster(getMaster());
        return id;
    }

    @Override
    public Job fetch(long id) {
        JobDTO h = null;
        try {
            h = getDTOmaster().Job().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        Job j = JobDTOFactory.getInstance().toModel(h);
        j.setMaster(this.getMaster());
        return j;
    }

    @Override
    public Iterator<Job> fetchall() {
        try {
            Iterator<JobDTO> fetchall = getDTOmaster().Job().fetchall();
            return new BaseIterator<JobDTO, Job>(fetchall) {
                @Override
                public Job next() {
                    Job j = JobDTOFactory.getInstance().toModel(iter.next());
                    j.setMaster(getMaster());
                    return j;
                }
            };

        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public void update(Job obj) {
        JobDTO dto = JobDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Job().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.modified();
    }

    @Override
    public Task delete(Job obj) {
        Task ret = null;
        try {
            UUID uuid = getDTOmaster().Job().delete(obj.getId());
            ret = getMaster().Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    public List<Job> ByAttributeTypeAndSeqRun(long atype_id, SeqRun run) {
        List<Job> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().ByAttributeTypeAndSeqRun(atype_id, run.getId())) {
                Job j = JobDTOFactory.getInstance().toModel(dto);
                j.setSeqrun(run);
                j.setMaster(this.getMaster());
                all.add(j);
            }
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
    }

    public List<Job> BySeqRun(SeqRun run) {
        List<Job> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().BySeqRun(run.getId())) {
                Job j = JobDTOFactory.getInstance().toModel(dto);
                j.setSeqrun(run);
                j.setMaster(this.getMaster());
                all.add(j);
            }
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
    }

    public String getErrorMessage(Job job) {
        try {
            MGXString err = getDTOmaster().Job().getError(job.getId());
            return err.getValue();
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
