/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Fetcher;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.api.visualization.ConflictResolver;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

/**
 *
 * @author sj
 */
public interface VGroupManagerI extends PropertyChangeListener {

    //String REPLICATEGROUP_NUM_CHANGED = "replGroupNumChanged";
    String REPLICATEGROUP_SELECTION_CHANGED = "replGroupSelectionChanged";
    //String VISGROUP_NUM_CHANGED = "vgNumChanged";
    String VISGROUP_ADDED = "vgmgr_visgroup_added";
    //String VISGROUP_REMOVED = "vgmgr_visgroup_removed";
    String REPLGROUP_ADDED = "vgmgr_replgroup_added";
    String ASMGROUP_ADDED = "vgmgr_assemblygroup_added";
    //String REPLGROUP_REMOVED = "vgmgr_replgroup_removed";
    String VISGROUP_SELECTION_CHANGED = "vgSelectionChanged";
    String ASMGROUP_SELECTION_CHANGED = "asmGroupSelectionChanged";

    void addPropertyChangeListener(PropertyChangeListener p);

    VisualizationGroupI createVisualizationGroup();

    List<GroupI> getActiveGroups();

    Collection<GroupI> getAllGroups();

    List<Pair<GroupI, DistributionI<Long>>> getDistributions() throws ConflictingJobsException;

    List<Pair<GroupI, TreeI<Long>>> getHierarchies() throws ConflictingJobsException;
    
    void setSelectedVisualizationGroup(VisualizationGroupI group);

    VisualizationGroupI getSelectedVisualizationGroup();

    void registerResolver(ConflictResolver cr);

    ConflictResolver getResolver();

    //void removeVisualizationGroup(VisualizationGroupI vg);
    void removePropertyChangeListener(PropertyChangeListener p);

    boolean selectAttributeType(AttributeRank rank, String aType);

    boolean selectAttributeType(String aType);

    String getSelectedAttributeType();

    Collection<AttributeTypeI> getAttributeTypes();

    <T> Future<T> submit(Fetcher<T> f);

    //
    // replicate group handling
    //
    Collection<ReplicateGroupI> getReplicateGroups();

    ReplicateGroupI createReplicateGroup();

    ReplicateI createReplicate(ReplicateGroupI rGroup);

    public void setSelectedReplicateGroup(ReplicateGroupI replicateGroup);

    public ReplicateGroupI getSelectedReplicateGroup();

    public GroupI getGroup(String displayName);

    public AssemblyGroupI createAssemblyGroup();

    public void setSelectedAssemblyGroup(AssemblyGroupI group);

}
