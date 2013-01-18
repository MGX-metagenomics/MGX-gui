package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public abstract class ModelBase implements Transferable {

    protected MGXMasterI master;
    protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    public final static String OBJECT_DELETED = "objectDeleted";
    public final static String OBJECT_MODIFIED = "objectModified";
    private final DataFlavor dataflavor;

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
