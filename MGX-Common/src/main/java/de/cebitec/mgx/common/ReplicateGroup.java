/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.common;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.model.ModelBaseI;
import static de.cebitec.mgx.api.model.ModelBaseI.OBJECT_DELETED;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 * @author sjaenick
 */
public class ReplicateGroup implements ReplicateGroupI {

    private String name;
    private Color color;
    private final Collection<ReplicateI> groups = new ArrayList<>();
    private boolean is_active = true;
    //
    private String managedState = OBJECT_MANAGED;
    //
    private final PropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this, true);
    //
    int replCnt = 1;

    ReplicateGroup(String name) {
        this.name = name;
    }

    @Override
    public void add(ReplicateI replicate) {
        if (replicate != null && !groups.contains(replicate)) {
            replicate.addPropertyChangeListener(this);
            groups.add(replicate);
            pcs.firePropertyChange(REPLICATEGROUP_REPLICATE_ADDED, null, replicate);
            modified();
        }
    }

    @Override
    public void remove(ReplicateI replicate) {
        if (replicate != null && groups.contains(replicate)) {
            replicate.removePropertyChangeListener(this);
            replicate.close();
            groups.remove(replicate);
            pcs.firePropertyChange(REPLICATEGROUP_REPLICATE_REMOVED, null, replicate);
            modified();
        }
    }

    @Override
    public boolean isEmpty() {
        return groups.isEmpty();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        modified();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        modified();
    }

    @Override
    public long getNumSequences() {
        if (!isActive()) {
            return 0;
        }
        long numSeq = 0;
        for (ReplicateI replicate : getReplicates()) {
            if (replicate.isActive()) {
                numSeq += replicate.getNumSequences();
            }
        }
        return numSeq;
    }

    @Override
    public final boolean isActive() {
        return is_active && !isDeleted();
    }

    @Override
    public final void setActive(boolean is_active) {
        this.is_active = is_active;
        pcs.firePropertyChange(is_active ? REPLICATEGROUP_ACTIVATED : REPLICATEGROUP_DEACTIVATED, !is_active, is_active);
    }

    @Override
    public void close() {
        for (ReplicateI r : groups) {
            r.removePropertyChangeListener(this);
            r.close();
            pcs.firePropertyChange(REPLICATEGROUP_REPLICATE_REMOVED, null, r);
        }
        groups.clear();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        pcs.firePropertyChange(evt);
    }

    @Override
    public Collection<ReplicateI> getReplicates() {
        return Collections.unmodifiableCollection(groups);
    }

    @Override
    public void modified() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot modify deleted object.");
        }
        firePropertyChange(ModelBaseI.OBJECT_MODIFIED, 1, 2);
    }

    @Override
    public void deleted() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot delete deleted object.");
        }
        firePropertyChange(ModelBaseI.OBJECT_DELETED, 0, 1);
        managedState = OBJECT_DELETED;
    }

    @Override
    public final boolean isDeleted() {
        return managedState.equals(OBJECT_DELETED);
    }

    @Override
    public final DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{ReplicateGroupI.DATA_FLAVOR};
    }

    @Override
    public final boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor != null && flavor.equals(ReplicateGroupI.DATA_FLAVOR);
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
    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public final void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
    }

    @Override
    public final void firePropertyChange(String propertyName, int oldValue, int newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
        //pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
    }

    @Override
    public final void firePropertyChange(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }

    @Override
    public int compareTo(ReplicateGroupI o) {
        return getName().compareTo(o.getName());
    }

}