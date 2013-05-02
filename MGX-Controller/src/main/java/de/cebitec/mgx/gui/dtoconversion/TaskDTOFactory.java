package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class TaskDTOFactory<T extends ModelBase> extends DTOConversionBase<Task, TaskDTO> {

    static {
        instance = new TaskDTOFactory();
    }
    protected static TaskDTOFactory instance;

    private TaskDTOFactory() {
    }
    
    private static ModelBase object;
    private static UUID taskUUid;

    public static synchronized TaskDTOFactory getInstance(ModelBase obj, UUID uuid) {
        object = obj;
        taskUUid = uuid;
        return instance;
    }

    @Override
    public TaskDTO toDTO(Task a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Task toModel(TaskDTO dto) {
        return new Task(object, taskUUid)
                .setStatusMessage(dto.getMessage())
                .setState(Task.State.values()[dto.getState().ordinal()]);
    }
}
