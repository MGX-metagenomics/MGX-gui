package de.cebitec.mgx.gui.datamodel.misc;

/**
 *
 * @author sjaenick
 */
public class Task {

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
}
