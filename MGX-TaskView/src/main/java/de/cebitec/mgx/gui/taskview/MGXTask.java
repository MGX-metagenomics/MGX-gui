package de.cebitec.mgx.gui.taskview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sjaenick
 */
public abstract class MGXTask implements Runnable, PropertyChangeListener {

    private final PropertyChangeSupport pcs;

    public MGXTask() {
        pcs = new PropertyChangeSupport(this);
    }

    @Override
    public void run() {
        process();
    }

    public abstract String getStatus();

    public abstract void process();

    public abstract void finished();

    public void failed() {
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
