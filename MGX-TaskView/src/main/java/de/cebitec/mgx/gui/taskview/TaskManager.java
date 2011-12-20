package de.cebitec.mgx.gui.taskview;

import de.cebitec.mgx.client.upload.SeqUploader;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author sjaenick
 */
public class TaskManager {

    private static TaskManager instance;
    private RequestProcessor rp = null;
    private Map<Task, TaskDescriptor> tasks = new HashMap<Task, TaskDescriptor>();
    private TaskViewTopComponent tc;
    private final PropertyChangeSupport pcs;

    private TaskManager() {
        pcs = new PropertyChangeSupport(this);
        rp = new RequestProcessor("Tasks", 5, true);
        tc = (TaskViewTopComponent) WindowManager.getDefault().findTopComponent("TaskViewTopComponent");
        tc.open();
        tc.requestActive();
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public List<TaskDescriptor> getActiveTasks() {
        List<TaskDescriptor> ret = new ArrayList(tasks.values());
        Collections.sort(ret);
        return ret;
    }

    public void removeTask() {
        fireTaskChange();
    }

    public void addUploadTask(final SeqUploader u) {
        TaskDescriptor td = new TaskDescriptor(u);
        Task task = rp.post(td);
        task.addTaskListener(new MyTaskListener());
        tasks.put(task, td);
        if (!tc.isOpened()) {
            tc.open();
            tc.requestActive();
        }
        fireTaskChange();
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }

    private void fireTaskChange() {
        pcs.firePropertyChange("FOO", 0, 1);
    }

    private final class MyTaskListener implements TaskListener {

        @Override
        public void taskFinished(org.openide.util.Task task) {
            //TaskDescriptor td = tasks.get(task);
            fireTaskChange();
        }
    }
}
