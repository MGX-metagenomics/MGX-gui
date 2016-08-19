/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.common;

import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.ModelBaseI;
import static de.cebitec.mgx.api.model.ModelBaseI.OBJECT_DELETED;
import de.cebitec.mgx.api.visualization.ConflictResolver;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class ReplicateGroup implements ReplicateGroupI {

    private final VGroupManagerI vgmgr;
    private String name;
    private Color color;
    private final Collection<ReplicateI> groups = new ArrayList<>();
    private boolean is_active = true;
    //
    private String managedState = OBJECT_MANAGED;
    //
    private final ParallelPropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this, true);
    //
    DistributionI<Double> meanDist = null;
    DistributionI<Double> stdvDist = null;

    int nextReplicateNum = 1;

    ReplicateGroup(VGroupManagerI vgmgr, String name) {
        this.vgmgr = vgmgr;
        this.name = name;
    }

    @Override
    public VGroupManagerI getManager() {
        return vgmgr;
    }

    @Override
    public void add(ReplicateI replicate) {
        if (replicate != null && !groups.contains(replicate)) {
            replicate.addPropertyChangeListener(this);
            synchronized (groups) {
                groups.add(replicate);
            }
            pcs.firePropertyChange(REPLICATEGROUP_REPLICATE_ADDED, null, replicate);
            modified();
        }
    }

    @Override
    public void remove(ReplicateI replicate) {
        if (replicate != null && groups.contains(replicate)) {
            replicate.removePropertyChangeListener(this);
            replicate.close();
            synchronized (groups) {
                groups.remove(replicate);
            }
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
        synchronized (groups) {
            for (ReplicateI replicate : groups) {
                if (replicate.isActive()) {
                    numSeq += replicate.getNumSequences();
                }
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
        ReplicateI[] tmp;
        synchronized (groups) {
            tmp = groups.toArray(new ReplicateI[]{});
        }
        for (ReplicateI r : tmp) {
            r.close();
            pcs.firePropertyChange(REPLICATEGROUP_REPLICATE_REMOVED, null, r);
        }

        synchronized (groups) {
            groups.clear();
        }
        deleted();
        pcs.close();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ModelBaseI.OBJECT_DELETED:
                if (evt.getSource() instanceof ReplicateI) {
                    ReplicateI r = (ReplicateI) evt.getSource();
                    r.removePropertyChangeListener(this);
                    synchronized (groups) {
                        groups.remove(r);
                    }
                }
                break;
        }
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

    @Override
    public DistributionI<Double> getMeanDistribution() {
//        if (meanDist != null)
//            return meanDist;

        Pair<DistributionI<Double>, DistributionI<Double>> dists;
        try {
            dists = calcDistributions();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(ReplicateGroup.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        meanDist = dists.getFirst();
        stdvDist = dists.getSecond();

        return meanDist;
    }

    @Override
    public DistributionI<Double> getStdvDistribution() {
//        if (stdvDist != null)
//            return stdvDist;

        Pair<DistributionI<Double>, DistributionI<Double>> dists;
        try {
            dists = calcDistributions();
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(ReplicateGroup.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        meanDist = dists.getFirst();
        stdvDist = dists.getSecond();

        return stdvDist;
    }

    private Pair<DistributionI<Double>, DistributionI<Double>> calcDistributions() throws InterruptedException, ExecutionException {
        ConflictResolver resolver = VGroupManager.getInstance().getResolver();
        assert resolver != null;

        Set<DistributionI<Long>> dists = new HashSet<>();
        List<VisualizationGroupI> conflicts = new ArrayList<>();
        for (ReplicateI rep : getReplicates()) {
            try {
                dists.add(rep.getDistribution());
            } catch (ConflictingJobsException ex) {
                conflicts.add(rep);
            }
        }

        if (!conflicts.isEmpty()) {
            resolver.resolve(VGroupManager.getInstance().getSelectedAttributeType(), conflicts);
        }

        return DistributionFactory.statisticalMerge(dists);
    }

    @Override
    public synchronized int getNextReplicateNum() {
        int ret = nextReplicateNum;
        nextReplicateNum++;
        return ret;
    }

}
