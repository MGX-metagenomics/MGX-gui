///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.api;
//
//import de.cebitec.mgx.api.exception.MGXException;
//import de.cebitec.mgx.api.model.MGXDataModelBaseI;
//import de.cebitec.mgx.api.model.ModelBaseI;
//import static de.cebitec.mgx.api.model.ModelBaseI.OBJECT_MANAGED;
//import de.cebitec.mgx.api.model.assembly.access.AssemblyAccessI;
//import de.cebitec.mgx.api.model.assembly.access.AssemblyJobAccessI;
//import de.cebitec.mgx.api.model.assembly.access.BinAccessI;
//import de.cebitec.mgx.api.model.assembly.access.ContigAccessI;
//import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
//import java.awt.datatransfer.DataFlavor;
//import java.awt.datatransfer.UnsupportedFlavorException;
//import java.beans.PropertyChangeEvent;
//import java.beans.PropertyChangeListener;
//import java.io.IOException;
//import java.util.Objects;
//import java.util.logging.Level;
//
///**
// *
// * @author sj
// */
//public abstract class MGXAssemblyMasterI implements MGXDataModelBaseI<MGXAssemblyMasterI> {
//
//    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MGXAssemblyMasterI.class, "MGXAssemblyMasterI");
//    private final ParallelPropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this, true);
//    private volatile String managedState = OBJECT_MANAGED;
//
//    private final MGXMasterI master;
//
//    public MGXAssemblyMasterI(MGXMasterI m) {
//        this.master = m;
//    }
//
//    @Override
//    public final MGXMasterI getMaster() {
//        return master;
//    }
//
//    public final void log(Level lvl, String msg) {
//        getMaster().log(lvl, msg);
//    }
//
//    public abstract AssemblyAccessI Assembly() throws MGXException;
//
//    public abstract BinAccessI Bin() throws MGXException;
//
//    public abstract ContigAccessI Contig() throws MGXException;
//
//    @Override
//    public final synchronized void modified() {
//        if (managedState.equals(OBJECT_DELETED)) {
//            throw new RuntimeException("Invalid object state, cannot modify deleted object.");
//        }
//        firePropertyChange(ModelBaseI.OBJECT_MODIFIED, 1, 2);
//    }
//
//    @Override
//    public final void childChanged() {
//        if (managedState.equals(OBJECT_DELETED)) {
//            throw new RuntimeException("Invalid object state for " + getClass().getSimpleName() + ", cannot modify deleted object.");
//        }
//        firePropertyChange(CHILD_CHANGE, 1, 2);
//    }
//
//    @Override
//    public final synchronized void deleted() {
//        if (managedState.equals(OBJECT_DELETED)) {
//            throw new RuntimeException("Invalid object state, cannot delete deleted object.");
//        }
//        System.err.println("sending deleted event to " + pcs.getPropertyChangeListeners().length + " listeners");
//        for (PropertyChangeListener pcl : pcs.getPropertyChangeListeners()) {
//            System.err.println("    target: " + pcl);
//        }
//        firePropertyChange(ModelBaseI.OBJECT_DELETED, 0, 1);
//        managedState = OBJECT_DELETED;
//        pcs.close();
//    }
//
//    @Override
//    public boolean isDeleted() {
//        return managedState.equals(OBJECT_DELETED) || master.isDeleted();
//    }
//
//    @Override
//    public final void addPropertyChangeListener(PropertyChangeListener listener) {
//        pcs.addPropertyChangeListener(listener);
//    }
//
//    @Override
//    public final void removePropertyChangeListener(PropertyChangeListener listener) {
//        pcs.removePropertyChangeListener(listener);
//    }
//
//    @Override
//    public final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
//        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
//        pcs.firePropertyChange(evt);
//    }
//
//    @Override
//    public final void firePropertyChange(String propertyName, int oldValue, int newValue) {
//        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
//        pcs.firePropertyChange(evt);
//        //pcs.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
//        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
//        pcs.firePropertyChange(evt);
//    }
//
////    @Override
//    public final void firePropertyChange(PropertyChangeEvent event) {
//        pcs.firePropertyChange(event);
//    }
//
//    @Override
//    public final DataFlavor[] getTransferDataFlavors() {
//        return new DataFlavor[]{DATA_FLAVOR};
//    }
//
//    @Override
//    public final boolean isDataFlavorSupported(DataFlavor flavor) {
//        return flavor != null && flavor.equals(DATA_FLAVOR);
//    }
//
//    @Override
//    public final Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
//        if (isDataFlavorSupported(flavor)) {
//            return this;
//        } else {
//            throw new UnsupportedFlavorException(flavor);
//        }
//    }
//
//    @Override
//    public final int compareTo(MGXAssemblyMasterI o) {
//        return getMaster().getProject().compareTo(o.getMaster().getProject());
//    }
//
//    @Override
//    public final int hashCode() {
//        int hash = 3;
//        hash = 59 * hash + Objects.hashCode(getMaster());
//        return hash;
//    }
//
//    @Override
//    public final boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final MGXAssemblyMasterI other = (MGXAssemblyMasterI) obj;
//        return Objects.equals(this.getMaster(), other.getMaster());
//    }
//
//}
