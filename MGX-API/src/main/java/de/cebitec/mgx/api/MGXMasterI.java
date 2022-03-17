package de.cebitec.mgx.api;

import de.cebitec.mgx.api.access.AttributeAccessI;
import de.cebitec.mgx.api.access.AttributeTypeAccessI;
import de.cebitec.mgx.api.access.DNAExtractAccessI;
import de.cebitec.mgx.api.access.FileAccessI;
import de.cebitec.mgx.api.access.HabitatAccessI;
import de.cebitec.mgx.api.access.JobAccessI;
import de.cebitec.mgx.api.access.MappingAccessI;
import de.cebitec.mgx.api.access.ObservationAccessI;
import de.cebitec.mgx.api.access.ReferenceAccessI;
import de.cebitec.mgx.api.access.ReferenceRegionAccessI;
import de.cebitec.mgx.api.access.SampleAccessI;
import de.cebitec.mgx.api.access.SeqRunAccessI;
import de.cebitec.mgx.api.access.SequenceAccessI;
import de.cebitec.mgx.api.access.StatisticsAccessI;
import de.cebitec.mgx.api.access.TaskAccessI;
import de.cebitec.mgx.api.access.TermAccessI;
import de.cebitec.mgx.api.access.ToolAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.assembly.access.AssemblyAccessI;
import de.cebitec.mgx.api.model.assembly.access.BinAccessI;
import de.cebitec.mgx.api.model.assembly.access.ContigAccessI;
import de.cebitec.mgx.api.model.assembly.access.GeneCoverageAccessI;
import de.cebitec.mgx.api.model.assembly.access.GeneObservationAccessI;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.logging.Level;
import de.cebitec.mgx.api.model.assembly.access.AssembledRegionAccessI;

/**
 *
 * @author sjaenick
 */
public abstract class MGXMasterI implements MGXDataModelBaseI<MGXMasterI> {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MGXMasterI.class, "MGXMasterI");
    //
    private final ParallelPropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this, true);
    private volatile String managedState = OBJECT_MANAGED;

    public MGXMasterI() {
        //super(null, dataflavor);
    }

    @Override
    public MGXMasterI getMaster() {
        return this;
    }

    public abstract String getServerName();

    public abstract void close();

//    public abstract RESTMembershipI getMembership();
    public abstract String getRoleName();

    public abstract String getProject();

    public abstract String getLogin();

    public abstract void log(Level lvl, String msg);

    public abstract HabitatAccessI Habitat() throws MGXException;

    public abstract SampleAccessI Sample() throws MGXException;

    public abstract DNAExtractAccessI DNAExtract() throws MGXException;

    public abstract SeqRunAccessI SeqRun() throws MGXException;

    public abstract ToolAccessI Tool() throws MGXException;

    public abstract JobAccessI Job() throws MGXException;

    public abstract AttributeAccessI Attribute() throws MGXException;

    public abstract AttributeTypeAccessI AttributeType() throws MGXException;

    public abstract ObservationAccessI Observation() throws MGXException;

    public abstract SequenceAccessI Sequence() throws MGXException;

    public abstract TermAccessI Term() throws MGXException;

    public abstract FileAccessI File() throws MGXException;

    public abstract ReferenceAccessI Reference() throws MGXException;

    public abstract ReferenceRegionAccessI ReferenceRegion() throws MGXException;

    public abstract MappingAccessI Mapping() throws MGXException;

    public abstract StatisticsAccessI Statistics() throws MGXException;

    public abstract <T extends MGXDataModelBaseI<T>> TaskAccessI<T> Task() throws MGXException;

    public abstract AssemblyAccessI Assembly() throws MGXException;

    public abstract BinAccessI Bin() throws MGXException;

    public abstract ContigAccessI Contig() throws MGXException;

    public abstract AssembledRegionAccessI AssembledRegion() throws MGXException;

    public abstract GeneCoverageAccessI GeneCoverage() throws MGXException;

    public abstract GeneObservationAccessI GeneObservation() throws MGXException;

    @Override
    public final synchronized void modified() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot modify deleted object.");
        }
        firePropertyChange(ModelBaseI.OBJECT_MODIFIED, 1, 2);
    }

    @Override
    public final void childChanged() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state for " + getClass().getSimpleName() + ", cannot modify deleted object.");
        }
        firePropertyChange(CHILD_CHANGE, 1, 2);
    }

    @Override
    public final synchronized void deleted() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot delete deleted object.");
        }
        log(Level.INFO, "sending deleted event from " + toString() + " to " + pcs.getPropertyChangeListeners().length + " listeners");
        for (PropertyChangeListener pcl : pcs.getPropertyChangeListeners()) {
            log(Level.INFO, "    target: " + pcl);
        }
        firePropertyChange(ModelBaseI.OBJECT_DELETED, 0, 1);
        managedState = OBJECT_DELETED;
        pcs.close();
    }

    @Override
    public boolean isDeleted() {
        return managedState.equals(OBJECT_DELETED);
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
        pcs.firePropertyChange(evt);
    }

    @Override
    public final void firePropertyChange(String propertyName, int oldValue, int newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        pcs.firePropertyChange(evt);
        //pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        pcs.firePropertyChange(evt);
    }

//    @Override
    public final void firePropertyChange(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }

    @Override
    public final DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DATA_FLAVOR};
    }

    @Override
    public final boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor != null && flavor.equals(DATA_FLAVOR);
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
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object o);

}
