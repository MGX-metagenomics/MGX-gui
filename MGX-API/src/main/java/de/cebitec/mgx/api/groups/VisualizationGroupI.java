/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.TreeI;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author sj
 */
public interface VisualizationGroupI extends ModelBaseI<VisualizationGroupI>, PropertyChangeListener {

    public static final DataFlavor VISGROUP_DATA_FLAVOR = new DataFlavor(VisualizationGroupI.class, "VisualizationGroupI");
    //private String managedState = OBJECT_MANAGED;
    //
    public static final String VISGROUP_ACTIVATED = "visgroup_activated";
    public static final String VISGROUP_ATTRTYPE_CHANGED = "vgAttrTypeChange";
    public static final String VISGROUP_CHANGED = "visgroup_changed";
    public static final String VISGROUP_DEACTIVATED = "visgroup_deactivated";
    public static final String VISGROUP_HAS_DIST = "vgroup_HasDist";
    public static final String VISGROUP_RENAMED = "vgroup_renamed";
    
    public VGroupManagerI getManager();

    public void addSeqRun(final SeqRunI sr);

    public void addSeqRuns(final SeqRunI... runs);

    public Iterator<AttributeTypeI> getAttributeTypes();

    public Color getColor();

    public List<Triple<AttributeRank, SeqRunI, Set<JobI>>> getConflicts();

    public Map<SeqRunI, Set<JobI>> getConflicts(AttributeRank rank);

    public DistributionI<Long> getDistribution() throws ConflictingJobsException;

    public TreeI<Long> getHierarchy() throws ConflictingJobsException;

    public int getId();

    public UUID getUUID();

    public void close();

    public String getName();

    public String getDisplayName();

    public long getNumSequences();

    public Map<SeqRunI, Set<AttributeI>> getSaveSet(List<String> requestedAttrs);

    public String getSelectedAttributeType();

    public List<SeqRunI> getSeqRuns();

    public boolean isActive();

    @Override
    public void propertyChange(PropertyChangeEvent evt);

//    void removePropertyChangeListener(PropertyChangeListener p);
    public void removeSeqRun(final SeqRunI sr);

    public void resolveConflict(AttributeRank rank, String attributeType, SeqRunI sr, JobI j);

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

    public void setActive(boolean is_active);

    public void setColor(Color color);

    public abstract void setName(String name);

    public int getNumberOfSeqRuns();

}
