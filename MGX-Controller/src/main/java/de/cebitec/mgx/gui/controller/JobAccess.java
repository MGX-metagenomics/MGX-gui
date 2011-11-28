package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.dtoadapter.JobDTOFactory;
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
        } catch (MGXServerException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    @Override
    public Job fetch(long id) {
        JobDTO h = null;
        try {
            h = getDTOmaster().Job().fetch(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return JobDTOFactory.getInstance().toModel(h);
    }

    @Override
    public List<Job> fetchall() {
        List<Job> all = new ArrayList<Job>();
        try {
            for (JobDTO dto : getDTOmaster().Job().fetchall()) {
                all.add(JobDTOFactory.getInstance().toModel(dto));
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return all;
    }

    @Override
    public void update(Job obj) {
        JobDTO dto = JobDTOFactory.getInstance().toDTO(obj);
        try {
            getDTOmaster().Job().update(dto);
        } catch (MGXServerException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(long id) {
        try {
            getDTOmaster().Job().delete(id);
        } catch (MGXServerException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(JobAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
