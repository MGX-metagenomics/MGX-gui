package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.TaskAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.TaskDTOFactory;
import java.util.UUID;

/**
 *
 * @author sjaenick
 * @param <T>
 */
public class TaskAccess<T extends MGXDataModelBaseI<T>> extends MasterHolder implements TaskAccessI<T> {


    public TaskAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TaskI<T> get(T obj, UUID taskId, TaskType tt) throws MGXException {
        TaskI<T> t = null;
        try {
            TaskDTO dto = getDTOmaster().Task().get(taskId);
            TaskDTOFactory fact = TaskDTOFactory.<T>getInstance(obj, taskId, tt);
            t = fact.toModel(getMaster(), dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return t;
    }

    @Override
    public void refresh(TaskI<T> origTask) throws MGXException {
        try {
            TaskDTO dto = getDTOmaster().Task().get(origTask.getUuid());
            origTask.setState(Task.State.values()[dto.getState().ordinal()]);
            origTask.setStatusMessage(dto.getMessage());
            //t = TaskDTOFactory.getInstance(origTask.getObject(), origTask.getUuid(), origTask.getTaskType()).toModel(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

}
