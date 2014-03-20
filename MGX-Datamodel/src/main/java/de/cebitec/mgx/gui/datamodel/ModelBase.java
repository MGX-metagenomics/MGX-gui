package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public abstract class ModelBase<T> implements Transferable, Comparable<T> {

    protected MGXMasterI master;
    //protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    public final static String OBJECT_DELETED = "objectDeleted";
    public final static String OBJECT_MODIFIED = "objectModified";
    private final DataFlavor dataflavor;
    //
    private final Set<WeakReference<PropertyChangeListener>> listeners = new HashSet<>();

    public ModelBase(DataFlavor dataflavor) {
        this.dataflavor = dataflavor;
    }

    public MGXMasterI getMaster() {
        return master;
    }

    public void setMaster(MGXMasterI m) {
        assert master == null; // prevent duplicate setting
        master = m;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{dataflavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor df : getTransferDataFlavors()) {
            if (df.equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public final void modified() {
        firePropertyChange(ModelBase.OBJECT_MODIFIED, this, null);
    }

    public final void deleted() {
        firePropertyChange(ModelBase.OBJECT_DELETED, this, null);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        boolean present = false;
        for (WeakReference<PropertyChangeListener> wr : listeners) {
              if (wr.get() != null && wr.get().equals(listener)) {
                present = true;
            }
        }
        if  (!present) {
            listeners.add(new WeakReference<>(listener));
        }
        //pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        WeakReference<PropertyChangeListener> removeMe = null;
        for (WeakReference<PropertyChangeListener> wr : listeners) {
            if (wr.get() != null && wr.get().equals(listener)) {
                removeMe = wr;
            }
        }
        listeners.remove(removeMe);
        //pcs.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
    }

    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
        //pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
        //pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(PropertyChangeEvent event) {
        //pcs.firePropertyChange(event);
        for (WeakReference<PropertyChangeListener> wr : listeners) {
            PropertyChangeListener target = wr.get();
            if (target != null) {
                target.propertyChange(event);
            }
        }
    }

    @Override
    public abstract int compareTo(T o);
}
