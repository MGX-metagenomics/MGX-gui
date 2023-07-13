/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.tree.TreeI;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author sj
 */
public interface GroupI<T> extends Comparable<GroupI<T>>, PropertyChangeListener {

    public final static String OBJECT_MANAGED = "objectManaged";
    public final static String OBJECT_DELETED = "objectDeleted";
    public final static String OBJECT_MODIFIED = "objectModified";
    public final static String CHILD_CHANGE = "childAddedOrRemoved";

    // possible property change names
    public static final String VISGROUP_ACTIVATED = "visgroup_activated";
    public static final String VISGROUP_ATTRTYPE_CHANGED = "vgAttrTypeChange";
    public static final String VISGROUP_CHANGED = "visgroup_changed";
    public static final String VISGROUP_DEACTIVATED = "visgroup_deactivated";
    public static final String VISGROUP_HAS_DIST = "vgroup_HasDist";
    public static final String VISGROUP_RENAMED = "vgroup_renamed";

    public Class<T> getContentClass();

    public int getId();

    public UUID getUUID();

    public VGroupManagerI getManager();

    public Iterator<AttributeTypeI> getAttributeTypes();

    /**
     *
     * @param rank
     * @param attrType
     * @throws ConflictingJobsException
     *
     * promote selection of an attribute type to the group; checks all contained
     * sequencing runs, i.) if they provide the attribute type and ii.) if the
     * attribute type is provided by a single job only. if several jobs are able
     * to provide the corresponding attribute type, a ConflictingJobsException
     * will be raised for resolval of the conflict.
     */
    public void selectAttributeType(AttributeRank rank, String attrType) throws ConflictingJobsException;

    public String getSelectedAttributeType();

    public String getName();

    public void setName(String name);

    public Color getColor();

    public void setColor(Color color);

    public String getDisplayName();

    public boolean isActive();

    public void setActive(boolean is_active);

    public DistributionI<Long> getDistribution() throws ConflictingJobsException;

    public TreeI<Long> getHierarchy() throws ConflictingJobsException;

    public List<Triple<AttributeRank, T, Set<JobI>>> getConflicts();

    public Map<T, Set<JobI>> getConflicts(AttributeRank rank);

    public Map<T, Set<AttributeI>> getSaveSet(List<String> requestedAttrs);

    public void resolveConflict(AttributeRank rank, String attributeType, T sr, JobI j);

    public void add(final T sr);

    @SuppressWarnings("unchecked")
    public void add(final T... runs);

    public Set<T> getContent();

    public void remove(final T sr);

    public long getNumSequences();

    public int getNumberOfSeqRuns();

    public List<T> getSeqRuns();

    public void close();

    public void childChanged();

    public void modified();

    public void deleted();

    public boolean isDeleted();

    public DataFlavor[] getTransferDataFlavors();

    public boolean isDataFlavorSupported(DataFlavor flavor);

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException;

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue);

    public void firePropertyChange(String propertyName, int oldValue, int newValue);

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue);

}
