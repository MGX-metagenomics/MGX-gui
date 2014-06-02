package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.ModelBase;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class TaskDTOFactory extends DTOConversionBase<TaskI, TaskDTO> {

    static {
        instance = new TaskDTOFactory();
    }
    protected static TaskDTOFactory instance;

    private TaskDTOFactory() {
    }
    private static ModelBase object;
    private static UUID taskUUid;
    private static TaskType taskType;

    public static synchronized TaskDTOFactory getInstance(ModelBase obj, UUID uuid, TaskType tType) {
        object = obj;
        taskUUid = uuid;
        taskType = tType;
        return instance;
    }

    @Override
    public TaskDTO toDTO(TaskI a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskI toModel(MGXMasterI m, TaskDTO dto) {
        return new Task(object, taskUUid, taskType)
                .setStatusMessage(dto.getMessage())
                .setState(Task.State.values()[dto.getState().ordinal()]);
    }
}
