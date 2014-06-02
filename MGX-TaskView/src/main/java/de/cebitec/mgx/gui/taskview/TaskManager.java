package de.cebitec.mgx.gui.taskview;

import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author sjaenick
 */
public class TaskManager implements TaskListener, PropertyChangeListener {

    public static final String TASK_ADDED = "TaskManager_TaskAdded";
    public static final String TASK_COMPLETED = "TaskManager_TaskCompleted";
    private static TaskManager instance;
    private final RequestProcessor reqProcessor;
    private final Map<org.openide.util.Task, MGXTask> currentTasks = new HashMap<>();
    private TaskViewTopComponent taskViewer;
    private final PropertyChangeSupport pcs;

    private TaskManager() {
        pcs = new ParallelPropertyChangeSupport(this);
        reqProcessor = new RequestProcessor("Tasks", Runtime.getRuntime().availableProcessors() + 5, true);
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public Collection<MGXTask> getActiveTasks() {
        return currentTasks.values();
    }

    public void addTask(final MGXTask mgxtask) {
        assert !EventQueue.isDispatchThread();
        
        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    // make sure the task viewer topcomponent is visible
                    if (taskViewer == null) {
                        taskViewer = getTaskViewer();
                    }
                    if (!taskViewer.isOpened()) {
                        taskViewer.open();
                    }
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }

        //mgxtask.addPropertyChangeListener(this);
        Task task = reqProcessor.post(mgxtask);
        currentTasks.put(task, mgxtask);
        task.addTaskListener(this);

        pcs.firePropertyChange(TASK_ADDED, 0, mgxtask);
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }

    private TaskViewTopComponent getTaskViewer() {
        TaskViewTopComponent t = (TaskViewTopComponent) WindowManager.getDefault().findTopComponent("TaskViewTopComponent");

        if (t == null) {
            t = Lookup.getDefault().lookup(TaskViewTopComponent.class);
        }
        return t;
    }

    @Override
    public void taskFinished(org.openide.util.Task task) {
        MGXTask completedTask = currentTasks.remove(task);
        assert completedTask != null;
        pcs.firePropertyChange(TASK_COMPLETED, 0, completedTask);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        pcs.firePropertyChange(evt);
    }
}
