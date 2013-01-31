package de.cebitec.mgx.gui.taskview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sjaenick
 */
public abstract class MGXTask implements Runnable, PropertyChangeListener {

    public static final String TASK_CHANGED = "MGXTask_Changed";
    public static final String TASK_FAILED = "MGXTask_Failed";
    public static final String TASK_FINISHED = "MGXTask_Finished";
    public static final int PROGRESS_UNKNOWN = -1;
    private final PropertyChangeSupport pcs;
    private final String taskName;
    private String statusMessage = "";
    private String state = TASK_CHANGED;

    public MGXTask(String name) {
        taskName = name;
        pcs = new PropertyChangeSupport(this);
        setStatus("Preparing..");
    }

    @Override
    public final void run() {
        fireTaskChanged();
        process();
    }

    public final String getStatus() {
        return statusMessage;
    }
    
    public final String getName() {
        return taskName;
    }

    protected final void setStatus(String s) {
        statusMessage = s;
        fireTaskChanged();
    }

    public abstract void process();

    public abstract boolean isDeterminate();

    
    /**
     *
     * @return a number between 0 and 100 indicating the progress
     * of this task, or MGXTask.PROGRESS_UNKNOWN 
     */
    public abstract int getProgress();

    public void finished() {
        setStatus("Done");
        state = TASK_FINISHED;
        fireTaskChanged();
    }

    public void failed() {
        setStatus("Failed");
        state = TASK_FAILED;
        fireTaskChanged();
    }

    private void fireTaskChanged() {
        pcs.firePropertyChange(state, 0, 1);
    }

    public final void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public final void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        pcs.firePropertyChange(pce);
    }
}
