package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobParameterDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO;
import de.cebitec.mgx.dto.dto.JobParameterListDTO.Builder;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.dtoconversion.JobDTOFactory;
import de.cebitec.mgx.gui.dtoconversion.JobParameterDTOFactory;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class JobAccess extends AccessBase<Job> {

    public boolean verify(long jobId) throws MGXServerException {
        return getDTOmaster().Job().verify(jobId);
    }

    public boolean execute(long jobId) throws MGXServerException {
        return getDTOmaster().Job().execute(jobId);
    }

    public boolean cancel(long jobId) throws MGXServerException {
        return getDTOmaster().Job().cancel(jobId);
    }

    public Iterable<JobParameter> getParameters(long jobId) throws MGXServerException {
        List<JobParameter> ret = new ArrayList<>();
        for (JobParameterDTO dto : getDTOmaster().Job().getParameters(jobId)) {
            ret.add(JobParameterDTOFactory.getInstance().toModel(dto));
        }
        return ret;
    }

    public void setParameters(long jobId, Iterable<JobParameter> params) throws MGXServerException {
        Builder b = JobParameterListDTO.newBuilder();
        for (JobParameter jp : params) {
            b.addParameter(JobParameterDTOFactory.getInstance().toDTO(jp));
        }
        getDTOmaster().Job().setParameters(jobId, b.build());
    }

    @Override
    public long create(Job obj) {
        JobDTO dto = JobDTOFactory.getInstance().toDTO(obj);
        long id = Identifiable.INVALID_IDENTIFIER;
        try {
            id = getDTOmaster().Job().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }

        obj.setMaster(this.getMaster());
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
    public List<Job> fetchall() {
        List<Job> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().fetchall()) {
                Job j = JobDTOFactory.getInstance().toModel(dto);
                j.setMaster(this.getMaster());
                all.add(j);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
    }

    @Override
    public void update(Job obj) {
        JobDTO dto = JobDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Job().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.firePropertyChange(ModelBase.OBJECT_MODIFIED, null, obj);
    }

    @Override
    public boolean delete(Job obj) {
        try {
            boolean ret = getDTOmaster().Job().delete(obj.getId());
            obj.firePropertyChange(ModelBase.OBJECT_DELETED, obj, null);
            return ret;
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    public List<Job> ByAttributeTypeAndSeqRun(long atype_id, long seqrun_id) {
        List<Job> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().ByAttributeTypeAndSeqRun(atype_id, seqrun_id)) {
                Job j = JobDTOFactory.getInstance().toModel(dto);
                j.setMaster(this.getMaster());
                all.add(j);
            }
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
    }

    public List<Job> BySeqRun(long seqrun_id) {
        List<Job> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().BySeqRun(seqrun_id)) {
                Job j = JobDTOFactory.getInstance().toModel(dto);
                j.setMaster(this.getMaster());
                all.add(j);
            }
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return all;
    }
}
