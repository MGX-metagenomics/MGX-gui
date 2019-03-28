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
    //String REPLGROUP_REMOVED = "vgmgr_replgroup_removed";
    String VISGROUP_SELECTION_CHANGED = "vgSelectionChanged";

    void addPropertyChangeListener(PropertyChangeListener p);

    VisualizationGroupI createVisualizationGroup();

    List<VisualizationGroupI> getActiveVisualizationGroups();

    Collection<VisualizationGroupI> getAllVisualizationGroups();
    
    List<Pair<VisualizationGroupI, DistributionI<Long>>> getDistributions() throws ConflictingJobsException;

    List<Pair<VisualizationGroupI, TreeI<Long>>> getHierarchies() throws ConflictingJobsException;

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

    //void removeReplicateGroup(ReplicateGroupI vg);

    //boolean hasReplicateGroup(String name);
    public void setSelectedReplicateGroup(ReplicateGroupI replicateGroup);

    public ReplicateGroupI getSelectedReplicateGroup();

    public VisualizationGroupI getVisualizationGroup(String displayName);

}
