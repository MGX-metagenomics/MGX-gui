package de.cebitec.mgx.gui.datamodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sjaenick
 */
public abstract class ModelBase {

    protected MGXMasterI master;
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public final static String OBJECT_DELETED = "objectDeleted";
    public final static String OBJECT_MODIFIED = "objectModified";

    public MGXMasterI getMaster() {
        return master;
    }

    public void setMaster(MGXMasterI m) {
        assert master == null; // prevent duplicate setting
        master = m;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }
}
