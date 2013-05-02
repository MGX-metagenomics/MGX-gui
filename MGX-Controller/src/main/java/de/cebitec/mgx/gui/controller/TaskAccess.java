package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.TaskDTOFactory;
import java.util.Iterator;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class TaskAccess<T extends ModelBase> extends AccessBase<Task> {

    public Task get(T obj, UUID taskId) {
        Task t = null;
        try {
            TaskDTO dto = getDTOmaster().Task().get(taskId);
            t = TaskDTOFactory.getInstance(obj, taskId).toModel(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return t;
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
    public Task delete(Task obj) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
