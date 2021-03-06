/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.model;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 *
 * @author sjaenick
 * @param <T>
 */
public abstract class MGXDataModelBase<T extends MGXDataModelBaseI<T>> implements MGXDataModelBaseI<T>, PropertyChangeListener {

    private final MGXMasterI master;
    private final DataFlavor dataflavor;
    private ParallelPropertyChangeSupport pcs;
    private volatile String managedState = OBJECT_MANAGED;

    public MGXDataModelBase(MGXMasterI master, DataFlavor dataFlavor) {
        this.master = master;
        this.dataflavor = dataFlavor;

        if (master != null) {
            master.addPropertyChangeListener(this);
        }
    }

    @Override
    public final void propertyChange(PropertyChangeEvent evt) {
        if (OBJECT_DELETED.equals(evt.getPropertyName())) {
            deleted();
        }
    }

    @Override
    public final MGXMasterI getMaster() {
        return master;
    }

    @Override
    public final DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{dataflavor};
    }

    @Override
    public final boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor != null && flavor.equals(dataflavor);
    }

    @Override
    public final Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public final void modified() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state for " + getClass().getSimpleName() + ", cannot modify deleted object.");
        }
        firePropertyChange(OBJECT_MODIFIED, 1, 2);
    }

    @Override
    public final void childChanged() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state for " + getClass().getSimpleName() + ", cannot modify deleted object.");
        }
        firePropertyChange(CHILD_CHANGE, 1, 2);
    }

    @Override
    public synchronized void deleted() {
        if (managedState.equals(OBJECT_DELETED)) {
            //return;
            throw new RuntimeException("Invalid object state for " + getClass().getSimpleName() + ", cannot delete deleted object.");
        }
        if (master != null) {
            master.removePropertyChangeListener(this);
//            master.removePropertyChangeListener(stateListener);
        }
        firePropertyChange(OBJECT_DELETED, 0, 1);
        if (pcs != null) {
            pcs.close();
            pcs = null;
        }
        managedState = OBJECT_DELETED;
        //System.err.println("instance "+getClass().getSimpleName()+" marked as deleted.");
    }

    @Override
    public final boolean isDeleted() {
        return master != null
                ? (master.isDeleted() || managedState.equals(OBJECT_DELETED))
                : managedState.equals(OBJECT_DELETED);
    }

    @Override
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        if (pcs == null) {
            synchronized (this) {
                if (pcs == null) {
                    pcs = new ParallelPropertyChangeSupport(this, false);
                }
            }
        }
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        if (pcs != null) {
            pcs.removePropertyChangeListener(listener);
        }
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (pcs != null) {
            PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
            pcs.firePropertyChange(evt);
        }
    }

    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if (pcs != null && !managedState.equals(OBJECT_DELETED)) {
            PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
            pcs.firePropertyChange(evt);
        }
        //pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (pcs != null) {
            PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
            pcs.firePropertyChange(evt);
        }
    }

//    @Override
    protected void firePropertyChange(PropertyChangeEvent event) {
        if (pcs != null) {
            pcs.firePropertyChange(event);
        }
    }

//    private class StateListener implements PropertyChangeListener {
//
//        @Override
//        public final void propertyChange(PropertyChangeEvent evt) {
//            if (OBJECT_DELETED.equals(evt.getPropertyName())) {
//                deleted();
//            }
//        }
//    }

}
