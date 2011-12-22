package de.cebitec.mgx.gui.taskview;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sjaenick
 */
public class TaskDescriptor implements Comparable<TaskDescriptor>, PropertyChangeListener {

    public static final String TASKDESCRIPTOR_CHANGE = "tdChanged";
    private MGXTask run;
    private TaskListEntry te;
    private long startTime;
    private long endTime;
    private final PropertyChangeSupport pcs;

    public TaskDescriptor(String tName, final MGXTask r) {
        startTime = System.currentTimeMillis();
        pcs = new PropertyChangeSupport(this);
        this.run = r;
        run.addPropertyChangeListener(this);

        te = new TaskListEntry();
        te.setMainText(tName);
    }

    public TaskListEntry getTaskEntry() {
        return te;
    }

    public void finished() {
        endTime = System.currentTimeMillis();
        te.finished();
        run.finished();
    }
    
    long getEndTime() {
        return endTime;
    }

    private long getStartTime() {
        return startTime;
    }

    @Override
    public int compareTo(TaskDescriptor compareObject) {
        if (getStartTime() < compareObject.getStartTime()) {
            return -1;
        } else if (getStartTime() == compareObject.getStartTime()) {
            return 0;
        } else {
            return 1;
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals(MGXTask.TASK_STATUSLINE_CHANGED)) {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    getTaskEntry().setDetailText((String) pce.getNewValue());
                    pcs.firePropertyChange(TASKDESCRIPTOR_CHANGE, 0, 1);
                }
            });
        } else if (pce.getPropertyName().equals(MGXTask.TASK_CHANGED)) {
            pcs.firePropertyChange(TASKDESCRIPTOR_CHANGE, 0, 1);
        }
    }
}
