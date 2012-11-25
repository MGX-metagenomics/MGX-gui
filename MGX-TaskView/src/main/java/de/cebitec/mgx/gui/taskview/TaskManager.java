package de.cebitec.mgx.gui.taskview;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author sjaenick
 */
public class TaskManager implements PropertyChangeListener {

    public static final String TASKMANAGER_CHANGE = "tmChange";
    private static TaskManager instance;
    private final RequestProcessor reqProcessor;
    private final Map<Task, TaskDescriptor> currentTasks = new HashMap<>();
    private TaskViewTopComponent taskViewer;
    private final PropertyChangeSupport pcs;

    private TaskManager() {
        pcs = new PropertyChangeSupport(this);
        reqProcessor = new RequestProcessor("Tasks", 5, true);
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public List<TaskDescriptor> getActiveTasks() {
        List<TaskDescriptor> ret = new ArrayList(currentTasks.values());
        Collections.sort(ret);
        return ret;
    }

    public void clearFinished() {
        boolean modified = false;
        List<Task> to_remove = new ArrayList<>();
        for (Entry<Task, TaskDescriptor> e : currentTasks.entrySet()) {
            // check if task is finished
            TaskDescriptor td = e.getValue();
            if (td.getEndTime() != null) {
                to_remove.add(e.getKey());
                //currentTasks.remove(e.getKey());
                modified = true;
            }
        }
        for (Task t : to_remove) {
            currentTasks.remove(t);
        }
        if (modified) {
            fireTaskManagerChange();
        }
    }

//    public void removeTask(TaskDescriptor td) {
//        boolean found = false;
//        for (Entry<Task, TaskDescriptor> e : currentTasks.entrySet()) {
//            if (e.getValue() == td) {
//                currentTasks.remove(e.getKey());
//                found = true;
//            }
//        }
//        if (found) {
//            fireTaskManagerChange();
//        }
//    }
    public void addTask(String taskName, final MGXTask run) {
        TaskDescriptor td = new TaskDescriptor(taskName, run);
        td.addPropertyChangeListener(this);
        Task task = reqProcessor.post(run);
        task.addTaskListener(new MyTaskListener());
        currentTasks.put(task, td);

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (taskViewer == null) {
                    taskViewer = getTaskViewer();
                }
                if (!taskViewer.isOpened()) {
                    taskViewer.open();
                    taskViewer.requestActive();
                }
                fireTaskManagerChange();
            }
        });
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }

    private void fireTaskManagerChange() {
        pcs.firePropertyChange(TASKMANAGER_CHANGE, 0, 1);
    }

    private TaskViewTopComponent getTaskViewer() {
        TaskViewTopComponent t = (TaskViewTopComponent) WindowManager.getDefault().findTopComponent("TaskViewTopComponent");

        if (t == null) {
            t = Lookup.getDefault().lookup(TaskViewTopComponent.class);
        }
        return t;
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        pcs.firePropertyChange(pce);
    }

    private final class MyTaskListener implements TaskListener {

        @Override
        public void taskFinished(org.openide.util.Task task) {
            TaskDescriptor td = currentTasks.get(task);
            if (task.isFinished()) {
                td.finished();
            } else {
                System.err.println("task NOT finished");
                assert false; // do we ever reach this?
            }
        }
    }
}
