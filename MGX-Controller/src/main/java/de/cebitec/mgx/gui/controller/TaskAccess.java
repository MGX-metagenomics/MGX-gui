package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.TaskDTOFactory;
import java.util.Iterator;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class TaskAccess extends AccessBase<Task> {

    public Task get(UUID taskId) {
        try {
            TaskDTO dto = getDTOmaster().Task().get(taskId);
            return TaskDTOFactory.getInstance().toModel(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public long create(Task obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Task fetch(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<Task> fetchall() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Task obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID delete(Task obj) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
