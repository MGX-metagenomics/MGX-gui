package de.cebitec.mgx.gui.dtoconversion;

import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.gui.datamodel.misc.Task;

/**
 *
 * @author sjaenick
 */
public class TaskDTOFactory extends DTOConversionBase<Task, TaskDTO> {

    static {
        instance = new TaskDTOFactory();
    }
    protected static TaskDTOFactory instance;

    private TaskDTOFactory() {
    }

    public static TaskDTOFactory getInstance() {
        return instance;
    }

    @Override
    public TaskDTO toDTO(Task a) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Task toModel(TaskDTO dto) {
        return new Task()
                .setStatusMessage(dto.getMessage())
                .setState(Task.State.values()[dto.getState().ordinal()]);
    }
}
