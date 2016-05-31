package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import java.util.UUID;

/**
 *
 * @author sjaenick
 * @param <T>
 */
public class Task<T extends MGXDataModelBaseI<T>> extends TaskI<T> {

    private String statusMessage = "";
    private State state;
    //
    private final T obj;
    private final UUID uuid;
    private final TaskType taskType;

    public Task(T obj, UUID uuid, TaskType tType) {
        this.obj = obj;
        this.uuid = uuid;
        this.taskType = tType;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public T getObject() {
        return obj;
    }

    @Override
    public TaskType getTaskType() {
        return taskType;
    }

    @Override
    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public TaskI<T> setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        return this;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public TaskI<T> setState(State state) {
        if (state != this.state) {
            this.state = state;
            if (!obj.isDeleted()) {
                obj.modified();
            }
            if (state == State.FINISHED) {
                finish();
            }
        }
        return this;
    }

    @Override
    public boolean done() {
        return state == State.FINISHED || state == State.FAILED;
    }

    @Override
    protected final void finish() {
        if (getState() == State.FINISHED) {
            switch (taskType) {
                case DELETE:
                    if (!obj.isDeleted()) {
                        obj.deleted();
                    }
                    break;
                case MODIFY:
                    if (!obj.isDeleted()) {
                        obj.modified();
                    }
                    break;
            }
        }
    }
}
