package de.cebitec.mgx.gui.taskview;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JProgressBar;

/**
 *
 * @author sjaenick
 */
public class TaskDescriptor implements Comparable<TaskDescriptor>, PropertyChangeListener {

    public static final String TASKDESCRIPTOR_CHANGE = "TaskDescriptorChanged";
    private MGXTask mgxTask;
    private TaskListEntry te;
    private long startTime;
    private Long endTime = null;
    private final PropertyChangeSupport pcs;

    public TaskDescriptor(String tName, final MGXTask t) {
        startTime = System.currentTimeMillis();
        pcs = new PropertyChangeSupport(this);
        this.mgxTask = t;
        mgxTask.addPropertyChangeListener(this);

        te = new TaskListEntry();
        te.setMainText(tName);
        if (mgxTask.isDeterminate()) {
            JProgressBar progress = te.getProgressBar();
            progress.setIndeterminate(false);
            progress.setMinimum(0);
            progress.setMaximum(100);
            progress.setValue(0);
        }
    }

    public TaskListEntry getTaskEntry() {
        return te;
    }

    public void finished() {
        endTime = System.currentTimeMillis();
        te.finished();
        mgxTask.finished();
    }

    public Long getEndTime() {
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

    private void fireTaskDescriptorChanged() {
        pcs.firePropertyChange(TASKDESCRIPTOR_CHANGE, 0, 1);
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals(MGXTask.TASK_CHANGED)) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getTaskEntry().setDetailText(mgxTask.getStatus());
                    JProgressBar progress = getTaskEntry().getProgressBar();
                    if (mgxTask.isDeterminate()) {
                        progress.setValue(mgxTask.getProgress());
                    }
                }
            });
        } else if (pce.getPropertyName().equals(MGXTask.TASK_FINISHED)) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getTaskEntry().setDetailText(mgxTask.getStatus());
                    getTaskEntry().finished();
                }
            });
        } else if (pce.getPropertyName().equals(MGXTask.TASK_FAILED)) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getTaskEntry().setDetailText(mgxTask.getStatus());
                    getTaskEntry().failed();
             
                }
            });
        } else {
            pcs.firePropertyChange(pce);
        }
    }
}
