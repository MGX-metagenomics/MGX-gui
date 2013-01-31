package de.cebitec.mgx.gui.taskview;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.cebitec.mgx.gui.taskview//TaskView//EN",
autostore = false)
@TopComponent.Description(preferredID = "TaskViewTopComponent",
//iconBase="SET/PATH/TO/ICON/HERE", 
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "right", openAtStartup = false)
@ActionID(category = "Window", id = "de.cebitec.mgx.gui.taskview.TaskViewTopComponent")
@ActionReference(path = "Menu/Window", position = 370)
@TopComponent.OpenActionRegistration(displayName = "#CTL_TaskViewAction",
preferredID = "TaskViewTopComponent")
@ServiceProvider(service = TaskViewTopComponent.class)
public final class TaskViewTopComponent extends TopComponent implements PropertyChangeListener {

    public TaskViewTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(TaskViewTopComponent.class, "CTL_TaskViewTopComponent"));
        setToolTipText(NbBundle.getMessage(TaskViewTopComponent.class, "HINT_TaskViewTopComponent"));
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);
        //
        // add menu to clear finished/failed tasks
        final JPopupMenu contextMenu = new JPopupMenu("Edit");
        contextMenu.add(makeMenuItem("Clear finished tasks"));
        tasklistpanel.setComponentPopupMenu(contextMenu);
        tasklistpanel.setInheritsPopupMenu(true);
    }

    private JMenuItem makeMenuItem(String label) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (completedTasks) {
                    for (TaskListEntry tle : completedTasks) {
                        tasklistpanel.remove(tle);
                    }
                    completedTasks.clear();
                }
                revalidate();
                repaint();
            }
        });
        return item;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollpane = new javax.swing.JScrollPane();
        tasklistpanel = new javax.swing.JPanel();

        scrollpane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tasklistpanel.setAlignmentX(0.0F);
        tasklistpanel.setAlignmentY(0.0F);
        tasklistpanel.setLayout(new java.awt.GridLayout(1, 1));
        scrollpane.setViewportView(tasklistpanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollpane;
    private javax.swing.JPanel tasklistpanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        createList();
        TaskManager.getInstance().addPropertyChangeListener(this);
    }

    @Override
    public void componentClosed() {
        TaskManager.getInstance().removePropertyChangeListener(this);
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }
    private Map<MGXTask, TaskListEntry> currentTasks = new HashMap<>();
    private final Collection<TaskListEntry> completedTasks = new ArrayList<>();

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        MGXTask task;
        switch (evt.getPropertyName()) {
            case TaskManager.TASK_ADDED:
                task = (MGXTask) evt.getNewValue();
                addTask(task);
                break;
            case TaskManager.TASK_COMPLETED:
                task = (MGXTask) evt.getNewValue();
                removeTask(task);
                break;
            default:
                System.err.println("TopComponent received PCE " + evt.getPropertyName());
                break;
        }
    }

    private void createList() {
        Collection<MGXTask> activeTasks = TaskManager.getInstance().getActiveTasks();
        int rows = activeTasks.size() < 10 ? 10 : activeTasks.size();
        tasklistpanel.removeAll();
        tasklistpanel.setLayout(new GridLayout(rows, 1));

        for (MGXTask task : activeTasks) {
            addTask(task);
        }
    }

    private void addTask(MGXTask t) {
        TaskListEntry tle = new TaskListEntry(t);
        currentTasks.put(t, tle);
        tasklistpanel.add(tle);
    }

    private void removeTask(MGXTask t) {
        TaskListEntry tle = currentTasks.remove(t);
        synchronized (completedTasks) {
            completedTasks.add(tle);
        }
    }
}