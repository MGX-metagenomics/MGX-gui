/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.api.groups;

import de.cebitec.mgx.api.misc.DistributionI;
import java.awt.datatransfer.DataFlavor;
import java.util.Collection;

/**
 *
 * @author sjaenick
 */
public interface ReplicateGroupI extends GroupI<ReplicateI> {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(ReplicateGroupI.class, "ReplicateGroupI");

    String REPLICATEGROUP_ACTIVATED = "replicateGroup_activated";
//    String VISGROUP_ATTRTYPE_CHANGED = "vgAttrTypeChange";
    String REPLICATEGROUP_CHANGED = "replicateGroup_changed";
    String REPLICATEGROUP_DEACTIVATED = "replicateGroup_deactivated";
//    String VISGROUP_HAS_DIST = "vgHasDist";
    String REPLICATEGROUP_RENAMED = "replicateGroup_renamed";
    String REPLICATEGROUP_REPLICATE_ADDED = "replicateGroup_replicate_added";
    String REPLICATEGROUP_REPLICATE_REMOVED = "replicateGroup_replicate_removed";

    Collection<ReplicateI> getReplicates();

    DistributionI<Double> getMeanDistribution();

    DistributionI<Double> getStdDevDistribution();

    int getNextReplicateNum();

    boolean isEmpty();

}
