package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.JobAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.MGXString;
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
public class JobAccess extends AccessBase<JobI> implements JobAccessI {

    public JobAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        super(master, dtomaster);
    }
    
    @Override
    public boolean verify(JobI obj) throws MGXException {
        assert obj.getId() != Identifiable.INVALID_IDENTIFIER;
        boolean ret;
        try {
            ret = getDTOmaster().Job().verify(obj.getId());
        } catch (MGXServerException ex) {
            throw new MGXException(ex);
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
        } catch (MGXServerException ex) {
            throw new MGXException(ex);
        }
        obj.modified();
        return ret;
    }

    @Override
    public TaskI restart(JobI job) throws MGXException {
        TaskI ret = null;
        try {
            UUID uuid = getDTOmaster().Job().restart(job.getId());
            ret = getMaster().Task().get(job, uuid, TaskType.MODIFY);
            job.modified();
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
        return ret;
    }

    @Override
    public boolean cancel(JobI obj) throws MGXException {
        assert obj.getId() != Identifiable.INVALID_IDENTIFIER;
        boolean ret;
        try {
            ret = getDTOmaster().Job().cancel(obj.getId());
        } catch (MGXServerException ex) {
            throw new MGXException(ex);
        }
        obj.modified();
        return ret;
    }

    @Override
    public long create(JobI obj) {
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

        return id;
    }

    @Override
    public JobI fetch(long id) {
        JobDTO h = null;
        try {
            h = getDTOmaster().Job().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        JobI j = JobDTOFactory.getInstance().toModel(getMaster(), h);
        return j;
    }

    @Override
    public Iterator<JobI> fetchall() {
        try {
            Iterator<JobDTO> fetchall = getDTOmaster().Job().fetchall();
            return new BaseIterator<JobDTO, JobI>(fetchall) {
                @Override
                public JobI next() {
                    JobI j = JobDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return j;
                }
            };

        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public void update(JobI obj) {
        JobDTO dto = JobDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Job().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.modified();
    }

    @Override
    public TaskI delete(JobI obj) {
        TaskI ret = null;
        try {
            UUID uuid = getDTOmaster().Job().delete(obj.getId());
            ret = getMaster().Task().get(obj, uuid, TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    @Override
    public List<JobI> ByAttributeTypeAndSeqRun(long atype_id, SeqRunI run) {
        List<JobI> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().ByAttributeTypeAndSeqRun(atype_id, run.getId())) {
                JobI j = JobDTOFactory.getInstance().toModel(getMaster(), dto);
                j.setSeqrun(run);
                all.add(j);
            }
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
    }

    @Override
    public List<JobI> BySeqRun(SeqRunI run) {
        List<JobI> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().BySeqRun(run.getId())) {
                JobI j = JobDTOFactory.getInstance().toModel(getMaster(), dto);
                j.setSeqrun(run);
                all.add(j);
            }
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
    }

    @Override
    public String getErrorMessage(JobI job) {
        try {
            MGXString err = getDTOmaster().Job().getError(job.getId());
            return err.getValue();
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
