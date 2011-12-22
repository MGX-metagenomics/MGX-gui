package de.cebitec.mgx.gui.taskview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sjaenick
 */
public abstract class MGXTask implements Runnable, PropertyChangeListener {
    
    public static final String TASK_CHANGED = "taskChanged";
    public static final String TASK_STATUSLINE_CHANGED = "taskStatusChanged";
    private final PropertyChangeSupport pcs;
    private String status;

    public MGXTask() {
        pcs = new PropertyChangeSupport(this);
    }

    @Override
    public void run() {
        process();
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String s) {
        final String old = status;
        status = s;
        pcs.firePropertyChange(TASK_STATUSLINE_CHANGED, old, status);
    }

    public abstract void process();

    public void finished() {
        pcs.firePropertyChange(TASK_CHANGED, 0, 1);
    }

    public void failed() {
        pcs.firePropertyChange(TASK_CHANGED, 0, 1);
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        pcs.firePropertyChange(pce);
    }
}
