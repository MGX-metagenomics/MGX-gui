package de.cebitec.mgx.gui.taskview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sjaenick
 */
public abstract class MGXTask implements Runnable, PropertyChangeListener {

    public static final String TASK_CHANGED = "MGXTaskChanged";
    public static final String TASK_FAILED = "MGXTaskFailed";
    public static final String TASK_FINISHED = "MGXTaskFinished";
    public static final int PROGRESS_UNKNOWN = -1;
    private final PropertyChangeSupport pcs;
    private String status = "";
    private boolean failed = false;

    public MGXTask() {
        pcs = new PropertyChangeSupport(this);
    }

    @Override
    public void run() {
        fireTaskChanged();
        process();
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String s) {
        status = s;
        fireTaskChanged();
    }

    public abstract void process();

    public abstract boolean isDeterminate();

    public abstract int getProgress();

    public void finished() {
        if (failed) {
            return;
        }
        setStatus("Done");
        pcs.firePropertyChange(TASK_FINISHED, 0, 1);
    }

    public void failed() {
        failed = true;
        setStatus("Failed");
        pcs.firePropertyChange(TASK_FAILED, 0, 1);
    }

    private void fireTaskChanged() {
        if (!failed) {
            pcs.firePropertyChange(TASK_CHANGED, 0, 1);
        }
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
