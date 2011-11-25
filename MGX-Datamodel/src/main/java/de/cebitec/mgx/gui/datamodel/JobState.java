package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public enum JobState {
    
    CREATED(0),
    VERIFIED(1),
    SUBMITTED(2),
    PENDING(3),
    RUNNING(4),
    FINISHED(5),
    FAILED(6),
    ABORTED(7),
    IN_DELETION(8);

    private int code;

    private JobState(int c) {
        code = c;
    }

    public int getValue() {
        return code;
    }
}
