package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.TaskType;
import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class TaskDTOFactory<T extends MGXDataModelBaseI<T>> extends DTOConversionBase<TaskI<T>, TaskDTO> {

    static {
        instance = new TaskDTOFactory();
    }
    protected static TaskDTOFactory instance;

    private TaskDTOFactory() {
    }
    private static MGXDataModelBaseI<?> object;
    private static UUID taskUUid;
    private static TaskType taskType;

    public static synchronized <T extends MGXDataModelBaseI<T>> TaskDTOFactory getInstance(T obj, UUID uuid, TaskType tType) {
        object = (MGXDataModelBaseI<T>) obj;
        taskUUid = uuid;
        taskType = tType;
        return instance;
    }

    @Override
    public TaskDTO toDTO(TaskI<T> a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public TaskI<T> toModel(MGXMasterI m, TaskDTO dto) {
        return new Task<>((T)object, taskUUid, taskType)
                .setStatusMessage(dto.getMessage())
                .setState(Task.State.values()[dto.getState().ordinal()]);
    }
}
