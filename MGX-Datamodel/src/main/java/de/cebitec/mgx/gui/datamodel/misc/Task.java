package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.gui.datamodel.ModelBase;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class Task<T extends ModelBase> {

    public enum TaskType {

        MODIFY,
        DELETE;
    }

    public enum State {

        INIT(0),
        PROCESSING(1),
        FAILED(2),
        FINISHED(3);
        private int code;

        private State(int c) {
            code = c;
        }

        public int getValue() {
            return code;
        }
    }
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

    public UUID getUuid() {
        return uuid;
    }

    public T getObject() {
        return obj;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Task setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        return this;
    }

    public State getState() {
        return state;
    }

    public Task setState(State state) {
        this.state = state;
        return this;
    }

    public boolean done() {
        return state == State.FINISHED || state == State.FAILED;
    }

    public void finish() {
        if (getState() == State.FINISHED) {
            switch (taskType) {
                case DELETE:
                    getObject().firePropertyChange(ModelBase.OBJECT_DELETED, getObject(), null);
                    break;
                case MODIFY:
                    getObject().firePropertyChange(ModelBase.OBJECT_MODIFIED, getObject(), null);
                    break;
            }
        }
    }
}
