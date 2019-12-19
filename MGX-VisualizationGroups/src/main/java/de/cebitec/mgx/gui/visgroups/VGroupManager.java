package de.cebitec.mgx.gui.visgroups;

import de.cebitec.mgx.api.groups.AssemblyGroupI;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Fetcher;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.api.visualization.ConflictResolver;
import de.cebitec.mgx.gui.pool.MGXPool;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 *
 * @author sj
 */
public class VGroupManager implements VGroupManagerI {

    //
    // The VGroupManager manages visualization groups, which can contain
    // an arbitrary number of sequencing runs and internally aggregate 
    // results. This type of aggregation is purely additive, i.e. no 
    // internal normalization or weighting is being done.
    //
    // Sequencing runs can be combined across project and even server
    // boundaries.
    //
    // The basic concept is to have visualization groups, which form
    // separate entities to be displayed. In addition, the VGroupManager
    // supports the concept of replicates, where several visualization groups
    // represent different datasets obtained from the same sample. (Single
    // datasets are internally represented as replicate groups containing just
    // one single visualization group.)
    //
    // Visualization groups can be customized (name, display color) as well as
    // temporarily disabled to exclude them from being included in charts.
    //
    private static VGroupManagerI instance = null;

    // LinkedHashSet keeps the order elements are added
    private final Collection<GroupI<SeqRunI>> vizGroups = new LinkedHashSet<>();
    private final Collection<GroupI<AssembledSeqRunI>> assemblyGroups = new LinkedHashSet<>();
    private final Collection<ReplicateGroupI> replicateGroups = new LinkedHashSet<>();
    private int vizGroupCount = 1;
    private int replicateGroupCount = 1;
    private int assemblyGroupCount = 1;
    private final ParallelPropertyChangeSupport pcs;

    private final Map<AttributeRank, String> currentAttributeType = new HashMap<>();
    private ConflictResolver resolver = null;

    private static final Color groupColors[] = {Color.decode("#1d72aa"), Color.decode("#c44440"), Color.decode("#8cbb4e"),
        Color.decode("#795892"), Color.decode("#0099b2"), Color.decode("#f38533"), Color.decode("#8baad1")};

    private VGroupManager() {
        pcs = new ParallelPropertyChangeSupport(this);
    }

    public synchronized static VGroupManagerI getInstance() {
        if (instance == null) {
            instance = new VGroupManager();
        }
        return instance;
    }

    public synchronized static VGroupManagerI getTestInstance() {
        return new VGroupManager();
    }

    @Override
    public synchronized void registerResolver(ConflictResolver cr) {
        if (resolver == null) {
            resolver = cr;
        } else {
            throw new RuntimeException("Cannot register additional ConflictResolver");
        }
    }

    @Override
    public ConflictResolver getResolver() {
        return resolver;
    }

    private boolean hasVizGroup(String name) {
        synchronized (vizGroups) {
            for (GroupI vg : vizGroups) {
                if (name.equals(vg.getDisplayName()) || name.equals(vg.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasAssemblyGroup(String name) {
        synchronized (assemblyGroups) {
            for (GroupI vg : assemblyGroups) {
                if (name.equals(vg.getDisplayName()) || name.equals(vg.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasReplicateGroup(String name) {
        synchronized (replicateGroups) {
            for (ReplicateGroupI rg : replicateGroups) {
                if (name.equals(rg.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<GroupI> getActiveGroups() {
        List<GroupI> ret = new ArrayList<>(vizGroups.size() + assemblyGroups.size());
        synchronized (vizGroups) {
            for (GroupI<SeqRunI> g : vizGroups) {
                if (g.isActive()) {
                    ret.add(g);
                }
            }
        }
        synchronized (assemblyGroups) {
            for (GroupI<AssembledSeqRunI> g : assemblyGroups) {
                if (g.isActive()) {
                    ret.add(g);
                }
            }
        }

        return ret;
    }

    @Override
    public boolean selectAttributeType(String aType) {
        return selectAttributeType(AttributeRank.PRIMARY, aType);
    }

    @Override
    public String getSelectedAttributeType() {
        return currentAttributeType.get(AttributeRank.PRIMARY);
    }

    @Override
    public Collection<AttributeTypeI> getAttributeTypes() {
        List<AttributeTypeI> ret = new ArrayList<>();
        synchronized (vizGroups) {
            for (GroupI<SeqRunI> vg : vizGroups) {
                Iterator<AttributeTypeI> it = vg.getAttributeTypes();
                while (it.hasNext()) {
                    AttributeTypeI at = it.next();
                    if (!ret.contains(at)) {
                        ret.add(at);
                    }
                }
            }
        }

        synchronized (assemblyGroups) {
            for (GroupI<AssembledSeqRunI> vg : assemblyGroups) {
                Iterator<AttributeTypeI> it = vg.getAttributeTypes();
                while (it.hasNext()) {
                    AttributeTypeI at = it.next();
                    if (!ret.contains(at)) {
                        ret.add(at);
                    }
                }
            }
        }

        Collections.sort(ret, new Comparator<AttributeTypeI>() {
            @Override
            public int compare(AttributeTypeI o1, AttributeTypeI o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return ret;
    }

    @Override
    public synchronized boolean selectAttributeType(AttributeRank rank, String aType) {
        if (aType == null || rank == null || resolver == null) {
            return false;
        }

        //
        // check if aType is changed at all
        //
//        synchronized (currentAttributeType) {
//            if (currentAttributeType.containsKey(rank) && aType.equals(currentAttributeType.get(rank))) {
//                return true;
//            }
//        }
        List<GroupI> conflicts = null;
        synchronized (vizGroups) {
            for (GroupI<SeqRunI> vg : vizGroups) {
                if (vg.isActive()) {
                    try {
                        vg.selectAttributeType(rank, aType);
                    } catch (ConflictingJobsException ex) {
                        if (conflicts == null) {
                            conflicts = new ArrayList<>();
                        }
                        conflicts.add(vg);
                    }
                }
            }
        }

        synchronized (assemblyGroups) {
            for (GroupI<AssembledSeqRunI> vg : assemblyGroups) {
                if (vg.isActive()) {
                    try {
                        vg.selectAttributeType(rank, aType);
                    } catch (ConflictingJobsException ex) {
                        if (conflicts == null) {
                            conflicts = new ArrayList<>();
                        }
                        conflicts.add(vg);
                    }
                }
            }
        }

        if (conflicts != null && !conflicts.isEmpty()) {
            resolver.resolve(aType, conflicts);
        }

        if (conflicts == null || conflicts.isEmpty()) {
            currentAttributeType.put(rank, aType); // remember attribute type selection
//            System.err.println("selectAttributeType for " + aType + ": true");
            return true;
        }

//        System.err.println("selectAttributeType for " + aType + ": false");
        currentAttributeType.put(rank, null);
        return false;
    }

    @Override
    public List<Pair<GroupI, DistributionI<Long>>> getDistributions() throws ConflictingJobsException {

        if (currentAttributeType.get(AttributeRank.PRIMARY) == null) {
            throw new RuntimeException("VGMGR: primary attribute type is null");
        }

        int numActiveGroups = 0;
        // make sure there are no unresolved ambiguities left
        synchronized (vizGroups) {
            for (GroupI<SeqRunI> vg : vizGroups) {
                if (vg.isActive()) {
                    numActiveGroups++;
                    Map<SeqRunI, Set<JobI>> conflicts = vg.getConflicts(AttributeRank.PRIMARY);
                    if (!conflicts.isEmpty()) {
//                        System.err.println("CONFLICT for " + vg.getName());
                        assert false;
                        return null;
                    }
                }
            }
        }

        synchronized (assemblyGroups) {
            for (GroupI<AssembledSeqRunI> vg : assemblyGroups) {
                if (vg.isActive()) {
                    numActiveGroups++;
                    Map<AssembledSeqRunI, Set<JobI>> conflicts = vg.getConflicts(AttributeRank.PRIMARY);
                    if (!conflicts.isEmpty()) {
//                        System.err.println("CONFLICT for " + vg.getName());
                        assert false;
                        return null;
                    }
                }
            }
        }

        List<Pair<GroupI, DistributionI<Long>>> ret = new ArrayList<>(numActiveGroups);
        synchronized (vizGroups) {
            for (GroupI<SeqRunI> vg : vizGroups) {
                if (vg.isActive()) {
                    DistributionI<Long> dist = vg.getDistribution();
                    if (dist != null && !dist.isEmpty()) {
                        ret.add(new Pair<>(vg, dist));
                    }
                }
            }
        }

        synchronized (assemblyGroups) {
            for (GroupI<AssembledSeqRunI> vg : assemblyGroups) {
                if (vg.isActive()) {
                    DistributionI<Long> dist = vg.getDistribution();
                    if (dist != null && !dist.isEmpty()) {
                        ret.add(new Pair<>(vg, dist));
                    }
                }
            }
        }

        return ret;
    }

    @Override
    public List<Pair<GroupI, TreeI<Long>>> getHierarchies() throws ConflictingJobsException {

        if (currentAttributeType.get(AttributeRank.PRIMARY) == null) {
            throw new RuntimeException("VGMGR: primary attribute type is null");
        }

        int numActiveGroups = 0;
        // make sure there are no unresolved ambiguities left
        synchronized (vizGroups) {
            for (GroupI<SeqRunI> vg : vizGroups) {
                if (vg.isActive()) {
                    numActiveGroups++;
                    Map<SeqRunI, Set<JobI>> conflicts = vg.getConflicts(AttributeRank.PRIMARY);
                    if (!conflicts.isEmpty()) {
                        assert false;
                        return null;
                    }
                }
            }
        }

        synchronized (assemblyGroups) {
            for (GroupI<AssembledSeqRunI> vg : assemblyGroups) {
                if (vg.isActive()) {
                    numActiveGroups++;
                    Map<AssembledSeqRunI, Set<JobI>> conflicts = vg.getConflicts(AttributeRank.PRIMARY);
                    if (!conflicts.isEmpty()) {
                        assert false;
                        return null;
                    }
                }
            }
        }

        List<Pair<GroupI, TreeI<Long>>> ret = new ArrayList<>(numActiveGroups);
        synchronized (vizGroups) {
            for (GroupI<SeqRunI> vg : vizGroups) {
                if (vg.isActive()) {
                    TreeI<Long> tree = vg.getHierarchy();
                    if (tree != null && !tree.isEmpty()) {
                        ret.add(new Pair<>(vg, tree));
                    }
                }
            }
        }

        synchronized (assemblyGroups) {
            for (GroupI<AssembledSeqRunI> vg : assemblyGroups) {
                if (vg.isActive()) {
                    TreeI<Long> tree = vg.getHierarchy();
                    if (tree != null && !tree.isEmpty()) {
                        ret.add(new Pair<>(vg, tree));
                    }
                }
            }
        }

        return ret;
    }

    @Override
    public VisualizationGroupI createVisualizationGroup() {
        VisualizationGroupI group;

        synchronized (vizGroups) {
            String newName = "Group " + vizGroupCount;
            while (hasVizGroup(newName)) {
                newName = "Group " + ++vizGroupCount;
            }
            Color groupColor = groupColors[(vizGroupCount - 1) % groupColors.length];
            group = new VisualizationGroup(this, vizGroupCount, newName, groupColor);
            try {
                group.selectAttributeType(AttributeRank.PRIMARY, currentAttributeType.get(AttributeRank.PRIMARY));
                group.selectAttributeType(AttributeRank.SECONDARY, currentAttributeType.get(AttributeRank.SECONDARY));
            } catch (ConflictingJobsException ex) {
                // unreachable since group is empty
            }
            group.addPropertyChangeListener(this);
            vizGroups.add(group);
            vizGroupCount++;
        }

        firePropertyChange(VISGROUP_ADDED, 0, group);

        // auto-select initial group
        if (vizGroups.size() == 1) {
            setSelectedVisualizationGroup(group);
        }

        return group;
    }

    @Override
    public ReplicateI createReplicate(ReplicateGroupI rGroup) {

        ReplicateI newGrp;
        int cnt = 1;
        String nameTemplate = "Replicate " + String.valueOf(cnt);
        while (hasReplicate(nameTemplate, rGroup)) {
            cnt++;
            nameTemplate = "Replicate " + String.valueOf(cnt);
        }

        newGrp = new Replicate(rGroup, this, vizGroupCount, nameTemplate, rGroup.getColor());
        try {
            newGrp.selectAttributeType(AttributeRank.PRIMARY, currentAttributeType.get(AttributeRank.PRIMARY));
            newGrp.selectAttributeType(AttributeRank.SECONDARY, currentAttributeType.get(AttributeRank.SECONDARY));
        } catch (ConflictingJobsException ex) {
            // unreachable since group is empty
        }
        newGrp.addPropertyChangeListener(this);
        rGroup.add(newGrp);
        vizGroupCount++;
        synchronized (vizGroups) {
            vizGroups.add(newGrp); // a replicate is a visualization group, as well
        }
        return newGrp;
    }

    private static boolean hasReplicate(String template, ReplicateGroupI rGroup) {
        for (ReplicateI r : rGroup.getReplicates()) {
            if (r.getDisplayName().equals(template) || r.getName().equals(template)) {
                return true;
            }
        }
        return false;
    }

//    @Override
//    public void removeVisualizationGroup(VisualizationGroupI vg) {
//        if (vg != null && vizGroups.contains(vg)) {
//            vg.close();
//        }
//    }
    private VisualizationGroupI selectedGroup = null;
    private ReplicateGroupI selectedReplicateGroup = null;
    private AssemblyGroupI selectedAssemblyGroup = null;

    @Override
    public final void setSelectedVisualizationGroup(VisualizationGroupI group) {
        if (group != null && selectedGroup != group && !group.isDeleted()) {
            selectedGroup = group;
            firePropertyChange(VISGROUP_SELECTION_CHANGED, null, selectedGroup);
        }
    }

    @Override
    public final VisualizationGroupI getSelectedVisualizationGroup() {
        return selectedGroup;
    }

    @Override
    public ReplicateGroupI createReplicateGroup() {
        ReplicateGroupI replGroup;

        synchronized (replicateGroups) {
            String newName = "Replicate Group " + replicateGroupCount;
            while (hasReplicateGroup(newName)) {
                replicateGroupCount++;
                newName = "Replicate Group " + replicateGroupCount;
            }
            Color groupColor = groupColors[(replicateGroupCount - 1) % groupColors.length];
            replGroup = new ReplicateGroup(this, newName);
            replGroup.setColor(groupColor);
            replicateGroups.add(replGroup);
            replGroup.addPropertyChangeListener(this);
        }

        firePropertyChange(REPLGROUP_ADDED, 0, replGroup);

        if (getSelectedReplicateGroup() == null) {
            setSelectedReplicateGroup(replGroup);
        }
        return replGroup;
    }

    @Override
    public AssemblyGroupI createAssemblyGroup() {
        AssemblyGroupI group;

        synchronized (assemblyGroups) {
            String newName = "Assemblygroup " + assemblyGroupCount;
            while (hasAssemblyGroup(newName)) {
                newName = "Assemblygroup " + ++assemblyGroupCount;
            }
            Color groupColor = groupColors[(assemblyGroupCount - 1) % groupColors.length];
            group = new AssemblyGroup(this, assemblyGroupCount, newName, groupColor);
            try {
                group.selectAttributeType(AttributeRank.PRIMARY, currentAttributeType.get(AttributeRank.PRIMARY));
                group.selectAttributeType(AttributeRank.SECONDARY, currentAttributeType.get(AttributeRank.SECONDARY));
            } catch (ConflictingJobsException ex) {
                // unreachable since group is empty
            }
            group.addPropertyChangeListener(this);
            assemblyGroups.add(group);
            assemblyGroupCount++;
        }

        firePropertyChange(ASMGROUP_ADDED, 0, group);

        // auto-select initial group
        if (assemblyGroups.size() == 1) {
            setSelectedAssemblyGroup(group);
        }

        return group;
    }

    @Override
    public final void setSelectedAssemblyGroup(AssemblyGroupI group) {
        if (group != null && selectedAssemblyGroup != group && !group.isDeleted()) {
            selectedAssemblyGroup = group;
            firePropertyChange(ASMGROUP_SELECTION_CHANGED, null, selectedGroup);
        }
    }

//    @Override
//    public void removeReplicateGroup(ReplicateGroupI rg) {
//        if (rg != null && replicateGroups.contains(rg)) {
//            rg.close();
//        }
//    }
    @Override
    public void setSelectedReplicateGroup(ReplicateGroupI replicateGroup) {
        if (replicateGroup != null && selectedReplicateGroup != replicateGroup && !replicateGroup.isDeleted()) {
            selectedReplicateGroup = replicateGroup;
            firePropertyChange(REPLICATEGROUP_SELECTION_CHANGED, null, selectedReplicateGroup);
        }
    }

    @Override
    public ReplicateGroupI getSelectedReplicateGroup() {
        return selectedReplicateGroup;
    }

    @Override
    public Collection<GroupI> getAllGroups() {
        List<GroupI> ret = new ArrayList<>(vizGroups.size());
        synchronized (vizGroups) {
            ret.addAll(vizGroups);
        }
        return ret;
    }

    @Override
    public GroupI getGroup(String displayName) {
        for (GroupI vGrp : vizGroups) {
            if (displayName.equals(vGrp.getDisplayName())) {
                return vGrp;
            }
        }
        return null;
    }

    @Override
    public Collection<ReplicateGroupI> getReplicateGroups() {
        List<ReplicateGroupI> ret = new ArrayList<>(replicateGroups.size());
        synchronized (replicateGroups) {
            for (ReplicateGroupI rg : replicateGroups) {
                if (rg.isActive()) {
                    ret.add(rg);
                }
            }
        }
        return ret;
    }

    @Override
    public <T> Future<T> submit(Fetcher<T> f) {
        return MGXPool.getInstance().submit(f);
    }

    @Override
    public final void propertyChange(PropertyChangeEvent pce) {
        //System.err.println("VGMGR got " + pce);
        switch (pce.getPropertyName()) {
            case ModelBaseI.OBJECT_DELETED:
                if (pce.getSource() instanceof VisualizationGroupI) {
                    VisualizationGroupI vg = (VisualizationGroupI) pce.getSource();

                    synchronized (vizGroups) {
                        if (vizGroups.contains(vg)) {
                            vizGroups.remove(vg);
                            vg.removePropertyChangeListener(this);
                        }
                    }
                    if (vg.equals(selectedGroup)) {
                        selectedGroup = null;
                        firePropertyChange(VISGROUP_SELECTION_CHANGED, vg, null);
                    }
                } else if (pce.getSource() instanceof ReplicateI) {
                    ReplicateI repl = (ReplicateI) pce.getSource();

                    synchronized (vizGroups) {
                        if (vizGroups.contains(repl)) {
                            vizGroups.remove(repl);
                            repl.removePropertyChangeListener(this);
                        }
                    }
                    if (repl.equals(selectedGroup)) {
                        selectedGroup = null;
                        firePropertyChange(VISGROUP_SELECTION_CHANGED, repl, null);
                    }
                } else if (pce.getSource() instanceof ReplicateGroupI) {
                    ReplicateGroupI rg = (ReplicateGroupI) pce.getSource();
                    synchronized (vizGroups) {
                        for (ReplicateI replicate : rg.getReplicates()) {
                            vizGroups.remove(replicate);
                            replicate.removePropertyChangeListener(this);
                            replicate.deleted();
                            vizGroupCount--;
                        }
                    }

                    synchronized (replicateGroups) {
                        if (replicateGroups.contains(rg)) {
                            replicateGroups.remove(rg);
                            rg.removePropertyChangeListener(this);
                        }
                    }
                    if (rg.equals(selectedReplicateGroup)) {
                        selectedReplicateGroup = null;
                        firePropertyChange(REPLICATEGROUP_SELECTION_CHANGED, rg, null);
                    }
                } else if (pce.getSource() instanceof AssemblyGroupI) {
                    AssemblyGroupI vg = (AssemblyGroupI) pce.getSource();

                    synchronized (assemblyGroups) {
                        if (assemblyGroups.contains(vg)) {
                            assemblyGroups.remove(vg);
                            vg.removePropertyChangeListener(this);
                        }
                    }
                    if (vg.equals(selectedAssemblyGroup)) {
                        selectedAssemblyGroup = null;
                        firePropertyChange(ASMGROUP_SELECTION_CHANGED, vg, null);
                    }
                }
                break;
        }
        pcs.firePropertyChange(pce);
    }

    private void firePropertyChange(String name, Object oldValue, Object newValue) {
        pcs.firePropertyChange(name, oldValue, newValue);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }

}
