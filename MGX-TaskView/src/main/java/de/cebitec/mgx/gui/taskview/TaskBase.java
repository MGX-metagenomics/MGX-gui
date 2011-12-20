package de.cebitec.mgx.gui.taskview;

import org.netbeans.api.progress.ProgressHandle;

/**
 *
 * @author sjaenick
 */
public abstract class TaskBase implements Runnable, Comparable<TaskBase> {

    @Override
    public abstract void run();

    public abstract ProgressHandle getProgressHandle();
    
    public abstract long getStartTime();

    @Override
    public int compareTo(TaskBase compareObject) {
        if (getStartTime() < compareObject.getStartTime()) {
            return -1;
        } else if (getStartTime() == compareObject.getStartTime()) {
            return 0;
        } else {
            return 1;
        }
    }
}
