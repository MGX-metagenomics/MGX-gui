package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.dtoconversion.JobDTOFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class JobAccess extends AccessBase<Job> {

    public boolean verify(Long jobId) throws MGXServerException {
        return getDTOmaster().Job().verify(jobId);
    }

    public boolean execute(Long jobId) throws MGXServerException {
        return getDTOmaster().Job().execute(jobId);
    }

    public boolean cancel(Long jobId) throws MGXServerException {
        return getDTOmaster().Job().cancel(jobId);
    }

    @Override
    public Long create(Job obj) {
        JobDTO dto = JobDTOFactory.getInstance().toDTO(obj);
        Long id = null;
        try {
            id = getDTOmaster().Job().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        obj.setId(id);
        obj.setMaster(this.getMaster());
        return id;
    }

    @Override
    public Job fetch(Long id) {
        JobDTO h = null;
        try {
            h = getDTOmaster().Job().fetch(id);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    @Override
    public void update(Job obj) {
        JobDTO dto = JobDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Job().update(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            getDTOmaster().Job().delete(id);
        } catch (MGXServerException | MGXClientException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Job> ByAttributeTypeAndSeqRun(Long atype_id, Long seqrun_id) {
        List<Job> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().ByAttributeTypeAndSeqRun(atype_id, seqrun_id)) {
                Job j = JobDTOFactory.getInstance().toModel(dto);
                j.setMaster(this.getMaster());
                all.add(j);
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    public List<Job> BySeqRun(SeqRun sr) {
        List<Job> all = new ArrayList<>();
        try {
            for (JobDTO dto : getDTOmaster().Job().BySeqRun(sr.getId())) {
                Job j = JobDTOFactory.getInstance().toModel(dto);
                j.setMaster(this.getMaster());
                all.add(j);
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }
}
