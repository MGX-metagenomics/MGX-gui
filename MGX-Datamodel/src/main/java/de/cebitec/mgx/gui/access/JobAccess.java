package de.cebitec.mgx.gui.access;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.datamodel.Job;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class JobAccess extends AccessBase<Job> {

    public boolean verify(Long jobId) throws MGXServerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean execute(Long jobId) throws MGXServerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean cancel(Long jobId) throws MGXServerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long create(Job obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Job fetch(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Job> fetchall() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Job obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
