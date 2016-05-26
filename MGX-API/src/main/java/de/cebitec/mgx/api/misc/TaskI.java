/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.misc;

import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import java.util.UUID;

/**
 *
 * @author sj
 */
public abstract class TaskI<T extends MGXDataModelBaseI<T>> {

    public enum TaskType {

        MODIFY,
        DELETE;
    }

    public enum State {

        INIT(0),
        PROCESSING(1),
        FAILED(2),
        FINISHED(3);
        private final int code;

        private State(int c) {
            code = c;
        }

        public int getValue() {
            return code;
        }
    }

    public TaskI() {
    }

    public abstract UUID getUuid();

    public abstract T getObject();

    public abstract TaskType getTaskType();

    public abstract String getStatusMessage();

    public abstract TaskI<T> setStatusMessage(String statusMessage);

    public abstract State getState();

    public abstract TaskI<T> setState(State state);

    public abstract boolean done();

    protected abstract void finish();

}
