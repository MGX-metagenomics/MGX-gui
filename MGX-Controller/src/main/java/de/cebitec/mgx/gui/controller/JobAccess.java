package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.JobAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.MGXString;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.dtoconversion.JobDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class JobAccess implements JobAccessI {

    private final MGXDTOMaster dtomaster;
    private final MGXMasterI master;

    public JobAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        this.dtomaster = dtomaster;
        this.master = master;
        if (master.isDeleted()) {
            throw new MGXLoggedoutException("You are disconnected.");
        }
    }

    protected MGXDTOMaster getDTOmaster() {
        return dtomaster;
    }

    protected MGXMasterI getMaster() {
        return master;
    }

    @Override
    public boolean verify(JobI obj) throws MGXException {
        assert obj.getId() != Identifiable.INVALID_IDENTIFIER;
        boolean ret;
        try {
            ret = getDTOmaster().Job().verify(obj.getId());
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        obj.modified();
        return ret;
    }

    @Override
    public boolean execute(JobI obj) throws MGXException {
        assert obj.getId() != Identifiable.INVALID_IDENTIFIER;
        boolean ret;
        try {
            ret = getDTOmaster().Job().execute(obj.getId());
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        obj.modified();
        return ret;
    }

    @Override
    public TaskI<JobI> restart(JobI job) throws MGXException {
        TaskI<JobI> ret = null;
        if (job.getSeqrun() == null) {
            throw new MGXException("Internal error: Job has no sequencing run.");
        }
        if (job.getTool() == null) {
            throw new MGXException("Internal error: Job has no tool.");
        }
        try {
            UUID uuid = getDTOmaster().Job().restart(job.getId());
            ret = getMaster().<JobI>Task().get(job, uuid, TaskType.MODIFY);
            job.modified();
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        return ret;
    }

    @Override
    public boolean cancel(JobI obj) throws MGXException {
        assert obj.getId() != Identifiable.INVALID_IDENTIFIER;
        boolean ret;
        try {
            ret = getDTOmaster().Job().cancel(obj.getId());
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        if (ret) {
            obj.modified();
        }
        return ret;
    }

    @Override
    public JobI create(ToolI tool, SeqRunI seqrun, List<JobParameterI> params) throws MGXException {

        JobI job = new Job(tool.getMaster());
        job.setCreator(tool.getMaster().getLogin());
        job.setTool(tool);
        job.setStatus(JobState.CREATED);
        job.setSeqrun(seqrun);
        job.setParameters(params);

        JobDTO dto = JobDTOFactory.getInstance().toDTO(job);
        long id;
        try {
            id = getDTOmaster().Job().create(dto);
            job.setId(id);
            job.modified();
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }

        return job;
    }

    @Override
    public JobI fetch(long id) throws MGXException {
        JobDTO h = null;
        try {
            h = getDTOmaster().Job().fetch(id);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        JobI j = JobDTOFactory.getInstance().toModel(getMaster(), h);
        return j;
    }

    @Override
    public Iterator<JobI> fetchall() throws MGXException {
        try {
            Iterator<JobDTO> fetchall = getDTOmaster().Job().fetchall();
            return new BaseIterator<JobDTO, JobI>(fetchall) {
                @Override
                public JobI next() {
                    JobI j = JobDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return j;
                }
            };

        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public void update(JobI obj) throws MGXException {
        JobDTO dto = JobDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Job().update(dto);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        obj.modified();
    }

    @Override
    public TaskI<JobI> delete(JobI obj) throws MGXException {
        try {
            UUID uuid = getDTOmaster().Job().delete(obj.getId());
            return getMaster().<JobI>Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public List<JobI> ByAttributeTypeAndSeqRun(AttributeTypeI atype, SeqRunI run) throws MGXException {
        List<JobI> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().byAttributeTypeAndSeqRun(atype.getId(), run.getId())) {
                JobI j = JobDTOFactory.getInstance().toModel(getMaster(), dto);
                j.setSeqrun(run);
                all.add(j);
            }
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        return all;
    }

    @Override
    public List<JobI> BySeqRun(SeqRunI run) throws MGXException {
        List<JobI> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().bySeqRun(run.getId())) {
                JobI j = JobDTOFactory.getInstance().toModel(getMaster(), dto);
                if (j != null) {
                    j.setSeqrun(run);
                    all.add(j);
                }
            }
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
        return all;
    }

    @Override
    public String getErrorMessage(JobI job) throws MGXException {
        try {
            MGXString err = getDTOmaster().Job().getError(job.getId());
            return err.getValue();
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

}
