package de.cebitec.mgx.api.access.datatransfer;

import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sj
 */
public abstract class TransferBaseI {

    private final PropertyChangeSupport pcs;
    public static final String NUM_ELEMENTS_TRANSFERRED = "numElementsTransferred";
    public static final String TRANSFER_FAILED = "transferFailed";
    public static final String TRANSFER_COMPLETED = "transferCompleted";
    //
    private volatile String error_message = "";

    public TransferBaseI() {
        this.pcs = new ParallelPropertyChangeSupport(this);
    }

    public final synchronized String getErrorMessage() {
        return error_message;
    }

    protected final synchronized void setErrorMessage(String msg) {
        error_message = msg;
    }

    protected void fireTaskChange(String propName, Object newVal) {
        pcs.firePropertyChange(propName, 0, newVal);
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }
}
