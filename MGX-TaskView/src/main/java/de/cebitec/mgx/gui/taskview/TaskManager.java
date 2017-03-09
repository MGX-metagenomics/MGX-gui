package de.cebitec.mgx.gui.taskview;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.TaskListener;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 *
 * @author sjaenick
 */
public class TaskManager implements TaskListener { //, PropertyChangeListener {

//    public static final String TASK_ADDED = "TaskManager_TaskAdded";
//    public static final String TASK_COMPLETED = "TaskManager_TaskCompleted";
    private static TaskManager instance;
    private final RequestProcessor reqProcessor;
    private final Map<org.openide.util.Task, MGXTask> currentTasks = new HashMap<>();
    private volatile TaskViewTopComponent taskViewerUI;
//    private final PropertyChangeSupport pcs;

    private TaskManager() {
//        pcs = new ParallelPropertyChangeSupport(this);
        reqProcessor = new RequestProcessor("Tasks", Runtime.getRuntime().availableProcessors() + 5, true);
    }

    public static synchronized TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public Collection<MGXTask> getActiveTasks() {
        return currentTasks.values();
    }

    public void addTask(final MGXTask mgxtask) {

        //
        // open UI, if necessary
        //
        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    // make sure the task viewer topcomponent is visible
                    if (taskViewerUI == null) {
                        taskViewerUI = getTaskViewer();
                    }
                    if (!taskViewerUI.isOpened()) {
                        Mode mode = WindowManager.getDefault().findMode("right");
                        if (mode != null) {
                            mode.dockInto(taskViewerUI);
                        }
                        taskViewerUI.open();
                    }
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }

        //mgxtask.addPropertyChangeListener(this);
        taskViewerUI.addTask(mgxtask);

        Task task = reqProcessor.post(mgxtask);
        task.addTaskListener(this);
        synchronized (currentTasks) {
            currentTasks.put(task, mgxtask);
        }

        //pcs.firePropertyChange(TASK_ADDED, 0, mgxtask);
    }

    @Override
    public void taskFinished(org.openide.util.Task task) {
        MGXTask completedTask = null;
        synchronized (currentTasks) {
            completedTask = currentTasks.remove(task);
        }
        taskViewerUI.completeTask(completedTask);
//        pcs.firePropertyChange(TASK_COMPLETED, 0, completedTask);
    }

    private TaskViewTopComponent getTaskViewer() {
        TaskViewTopComponent t = (TaskViewTopComponent) WindowManager.getDefault().findTopComponent("TaskViewTopComponent");
        if (t == null) {
            t = new TaskViewTopComponent();
        }
        return t;
    }

//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        pcs.firePropertyChange(evt);
//    }
//
//    public void addPropertyChangeListener(PropertyChangeListener p) {
//        pcs.addPropertyChangeListener(p);
//    }
//
//    public void removePropertyChangeListener(PropertyChangeListener p) {
//        pcs.removePropertyChangeListener(p);
//    }
}
