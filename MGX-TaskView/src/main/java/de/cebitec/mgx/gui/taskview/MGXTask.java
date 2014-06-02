package de.cebitec.mgx.gui.taskview;

import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;
import java.util.UUID;

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
    private final UUID uuid = UUID.randomUUID();

    public MGXTask(String name) {
        taskName = name;
        pcs = new ParallelPropertyChangeSupport(this);
        setStatus("Preparing..");
    }

    @Override
    public final void run() {
        fireTaskChanged();
        if (process()) {
            finished();
        } else {
            failed();
        }
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

    public abstract boolean process();

    public boolean isDeterminate() {
        return false;
    }

    /**
     *
     * @return a number between 0 and 100 indicating the progress of this task, or MGXTask.PROGRESS_UNKNOWN
     */
    public int getProgress() {
        return MGXTask.PROGRESS_UNKNOWN;
    }

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
    
    protected void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        pcs.firePropertyChange(pce);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MGXTask other = (MGXTask) obj;
        return Objects.equals(this.uuid, other.uuid);
    }
}
