package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.TaskAccessI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.ModelBase;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.TaskDTOFactory;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 * @param <T>
 */
public class TaskAccess<T extends ModelBase> implements TaskAccessI<T> {

    private final MGXMasterI master;
    private final MGXDTOMaster dtomaster;

    public TaskAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        this.master = master;
        this.dtomaster = dtomaster;
    }


    @Override
    public TaskI get(T obj, UUID taskId, TaskType tt) {
        TaskI t = null;
        try {
            TaskDTO dto = dtomaster.Task().get(taskId);
            t = TaskDTOFactory.getInstance(obj, taskId, tt).toModel(master, dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return t;
    }

    @Override
    public TaskI refresh(TaskI origTask) {
        try {
            TaskDTO dto = dtomaster.Task().get(origTask.getUuid());
            origTask.setState(Task.State.values()[dto.getState().ordinal()]);
            origTask.setStatusMessage(dto.getMessage());
            //t = TaskDTOFactory.getInstance(origTask.getObject(), origTask.getUuid(), origTask.getTaskType()).toModel(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return origTask;
    }

}
