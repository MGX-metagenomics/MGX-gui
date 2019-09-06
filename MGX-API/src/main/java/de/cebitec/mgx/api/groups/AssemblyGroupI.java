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
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
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
public interface AssemblyGroupI extends GroupI<AssemblyGroupI>, PropertyChangeListener {

    public static final DataFlavor VISGROUP_DATA_FLAVOR = new DataFlavor(AssemblyGroupI.class, "VisualizationGroupI");
    //private String managedState = OBJECT_MANAGED;
    //
    public static final String ASMGROUP_ACTIVATED = "asmgroup_activated";
    public static final String ASMGROUP_ATTRTYPE_CHANGED = "asmgAttrTypeChange";
    public static final String ASMGROUP_CHANGED = "asmgroup_changed";
    public static final String ASMGROUP_DEACTIVATED = "asmgroup_deactivated";
    public static final String ASMGROUP_HAS_DIST = "asmgroup_HasDist";
    public static final String ASMGROUP_RENAMED = "asmgroup_renamed";
    
    public VGroupManagerI getManager();

    public void addSeqRun(final AssembledSeqRunI sr);

    public void addSeqRuns(final AssembledSeqRunI... runs);

    public Iterator<AttributeTypeI> getAttributeTypes();

    public Color getColor();

    public List<Triple<AttributeRank, SeqRunI, Set<JobI>>> getConflicts();

    public Map<SeqRunI, Set<JobI>> getConflicts(AttributeRank rank);

    public DistributionI<Long> getDistribution() throws ConflictingJobsException;

    public TreeI<Long> getHierarchy() throws ConflictingJobsException;

    public int getId();

    public UUID getUUID();

    public void close();

    public long getNumSequences();

    public Map<SeqRunI, Set<AttributeI>> getSaveSet(List<String> requestedAttrs);

    public String getSelectedAttributeType();

    public Set<AssembledSeqRunI> getSeqRuns();

    @Override
    public void propertyChange(PropertyChangeEvent evt);

//    void removePropertyChangeListener(PropertyChangeListener p);
    public void removeSeqRun(final AssembledSeqRunI sr);

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

}
