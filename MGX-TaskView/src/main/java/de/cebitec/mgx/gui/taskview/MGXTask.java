package de.cebitec.mgx.gui.taskview;

import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    private final ParallelPropertyChangeSupport pcs;
    private String taskName;
    private volatile String statusMessage = null;
    private String state = TASK_CHANGED;
    private final UUID uuid = UUID.randomUUID();
    private volatile boolean processingDone = false;

    public MGXTask(String name) {
        taskName = name;
        pcs = new ParallelPropertyChangeSupport(this);
        setStatus("Queued..");
    }
    
    public void setTaskName(String name) {
        this.taskName = name;
    }

    @Override
    public final void run() {
        pcs.firePropertyChange(state, 0, 1);
        if (process()) {
            finished();
        } else {
            failed(getStatus());
        }
    }

    public final synchronized String getStatus() {
        return statusMessage;
    }

    public final String getName() {
        return taskName;
    }

    protected final synchronized void setStatus(String s) {
        if (s != null && !"".equals(s.trim())) {
            if (statusMessage == null || !s.equals(statusMessage)) {
                statusMessage = s;
                pcs.firePropertyChange(state, 0, statusMessage);
            }
        }
    }

    public abstract boolean process();

    public boolean isDeterminate() {
        return false;
    }

    public final void dispose() {
        pcs.close();
    }

    /**
     *
     * @return a number between 0 and 100 indicating the progress of this task,
     * or MGXTask.PROGRESS_UNKNOWN
     */
    public int getProgress() {
        return MGXTask.PROGRESS_UNKNOWN;
    }

    public synchronized void finished() {
        if (!processingDone) {
            state = TASK_FINISHED;
            processingDone = true;
            setStatus("Done");
        }
    }

    public synchronized void failed(String reason) {
        if (!processingDone) {
            processingDone = true;
            setStatus("Failed: " + reason);
            state = TASK_FAILED;
            pcs.firePropertyChange(state, 0, reason);
        }
    }

    final void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    final void removePropertyChangeListener(PropertyChangeListener p) {
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
