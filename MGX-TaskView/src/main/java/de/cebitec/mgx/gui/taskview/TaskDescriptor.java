package de.cebitec.mgx.gui.taskview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;

/**
 *
 * @author sjaenick
 */
public class TaskDescriptor implements Comparable<TaskDescriptor>, PropertyChangeListener {

    private MGXTask run;
//    private ProgressHandle ph;
    private TaskListEntry te;
    private long startTime;
    private final PropertyChangeSupport pcs;

    public TaskDescriptor(String tName, final MGXTask r) {
        startTime = System.currentTimeMillis();
        pcs = new PropertyChangeSupport(this);
        this.run = r;
        run.addPropertyChangeListener(this);

        //
//        ph = ProgressHandleFactory.createHandle(tName);
        te = new TaskListEntry();
        te.setMainText(tName);
//        te.setDetailText(r.getStatus());

//        te.setMainText(ProgressHandleFactory.createMainLabelComponent(ph));
//        //te.setDetailText(ProgressHandleFactory.createDetailLabelComponent(ph));
//        te.setDetailText(new JLabel(run.getStatus()));
//        te.setProgressBar(ProgressHandleFactory.createProgressComponent(ph));
//        te.build();
        //
//        ph.start();
//        ph.switchToIndeterminate();
    }

    public JComponent getTaskEntry() {
        te.setDetailText(run.getStatus());
        return te;
    }

    public void finished() {
//        ph.finish();
        run.finished();
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
    public void propertyChange(PropertyChangeEvent pce) {
        pcs.firePropertyChange(pce);
    }
}
