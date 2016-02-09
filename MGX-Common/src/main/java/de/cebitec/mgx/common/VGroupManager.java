package de.cebitec.mgx.common;

import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Fetcher;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.api.visualization.ConflictResolver;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import org.openide.util.RequestProcessor;

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
    private final List<VisualizationGroupI> vizGroups = new ArrayList<>();
    private final List<ReplicateGroupI> replicateGroups = new ArrayList<>(5);
    private int vizGroupCount = 1;
    private int replicateGroupCount = 1;
    private final PropertyChangeSupport pcs;
    final RequestProcessor pool;

    private final Map<AttributeRank, String> currentAttributeType = new HashMap<>();
    private ConflictResolver resolver = null;

    private static final Color colors[] = {Color.decode("#1d72aa"), Color.decode("#c44440"), Color.decode("#8cbb4e"), 
        Color.decode("#795892"), Color.decode("#0099b2"), Color.decode("#f38533"), Color.decode("#8baad1")};
    
    private VGroupManager() {
        pcs = new ParallelPropertyChangeSupport(this);
        //
        // limit pool size to 20
        //
        pool = new RequestProcessor("VGroupTasks", Math.min(20, Runtime.getRuntime().availableProcessors() + 3));
    }

    public synchronized static VGroupManagerI getInstance() {
        if (instance == null) {
            instance = new VGroupManager();
        }
        return instance;
    }
    
    public static VGroupManagerI getTestInstance() {
        return new VGroupManager();
    }

    @Override
    public void registerResolver(ConflictResolver cr) {
        resolver = cr;
    }

    private boolean hasVizGroup(String name) {
        for (VisualizationGroupI vg : vizGroups) {
            if (name.equals(vg.getDisplayName()) || name.equals(vg.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasReplicateGroup(String name) {
        for (ReplicateGroupI rg : replicateGroups) {
            if (name.equals(rg.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<VisualizationGroupI> getActiveVisualizationGroups() {
        List<VisualizationGroupI> ret = new ArrayList<>();
        for (VisualizationGroupI g : vizGroups) {
            if (g.isActive()) {
                ret.add(g);
            }
        }
        return ret;
    }

    @Override
    public Collection<VisualizationGroupI> getAllVisualizationGroups() {
        List<VisualizationGroupI> ret = new ArrayList<>(vizGroups.size());
        ret.addAll(vizGroups);
        return ret;
    }

    @Override
    public boolean selectAttributeType(AttributeRank rank, String aType) {
        if (aType == null) {
            return false;
        }

        assert resolver != null;

        if (!aType.equals(currentAttributeType.get(rank))) {
            List<VisualizationGroupI> conflicts = new ArrayList<>();
            for (VisualizationGroupI vg : getActiveVisualizationGroups()) {
                try {
                    vg.selectAttributeType(rank, aType);
                } catch (ConflictingJobsException ex) {
                    conflicts.add(vg);
                }
            }

            if (!conflicts.isEmpty()) {
                resolver.resolve(conflicts);
            }

            return conflicts.isEmpty();
        }

        return true;
    }

    @Override
    public List<Pair<VisualizationGroupI, DistributionI<Long>>> getDistributions() throws ConflictingJobsException {
        List<Pair<VisualizationGroupI, DistributionI<Long>>> ret = new ArrayList<>(getActiveVisualizationGroups().size());
        for (VisualizationGroupI vg : getActiveVisualizationGroups()) {
            DistributionI<Long> dist = vg.getDistribution();
            if (!dist.isEmpty()) {
                ret.add(new Pair<>(vg, dist));
            }
        }
        return ret;
    }

    @Override
    public List<Pair<VisualizationGroupI, TreeI<Long>>> getHierarchies() {
        // make sure there are no unresolved ambiguities left
        for (VisualizationGroupI vg : getActiveVisualizationGroups()) {
            Map<SeqRunI, Set<JobI>> conflicts = vg.getConflicts(AttributeRank.PRIMARY);
            if (!conflicts.isEmpty()) {
                return null;
            }
        }

        List<Pair<VisualizationGroupI, TreeI<Long>>> ret = new ArrayList<>();
        for (VisualizationGroupI vg : getActiveVisualizationGroups()) {
            TreeI<Long> tree = vg.getHierarchy();
            if (tree != null && !tree.isEmpty()) {
                ret.add(new Pair<>(vg, tree));
            }
        }
        return ret;
    }

    @Override
    public VisualizationGroupI createVisualizationGroup() {
        VisualizationGroupI group = null;

        synchronized (vizGroups) {
            String newName = "Group " + vizGroupCount;
            while (hasVizGroup(newName)) {
                newName = "Group " + ++vizGroupCount;
            }
            Color groupColor = colors[(vizGroupCount - 1) % colors.length];
            group = new VisualizationGroup(this, vizGroupCount, newName, groupColor);
            group.addPropertyChangeListener(this);
            vizGroups.add(group);
            vizGroupCount++;
        }
        firePropertyChange(VISGROUP_ADDED, 0, group);
        return group;
    }

    @Override
    public ReplicateI createReplicate(ReplicateGroupI rGroup) {
        Replicate newGrp = null;

        int cnt = 1;
        String nameTemplate = "Replicate " + String.valueOf(cnt);
        while (hasReplicate(nameTemplate, rGroup)) {
            cnt++;
            nameTemplate = "Replicate " + String.valueOf(cnt);
        }

        newGrp = new Replicate(rGroup, this, vizGroupCount, nameTemplate, rGroup.getColor());
        newGrp.addPropertyChangeListener(this);
        rGroup.add(newGrp);
        vizGroupCount++;
        vizGroups.add(newGrp); // a replicate is a visualization group, as well
        return newGrp;
    }

    private boolean hasReplicate(String template, ReplicateGroupI rGroup) {
        for (ReplicateI r : rGroup.getReplicates()) {
            if (r.getDisplayName().equals(template) || r.getName().equals(template)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeVisualizationGroup(VisualizationGroupI vg) {
        if (vg != null && vizGroups.contains(vg)) {
            synchronized (vizGroups) {
                vg.close();
                vizGroups.remove(vg);
                vg.deleted();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        pcs.firePropertyChange(pce);
    }

//    private VisualizationGroupI getGroup(String name) {
//        for (VisualizationGroupI vg : groups.values()) {
//            if (name.equals(vg.getName())) {
//                return vg;
//            }
//        }
//        //System.err.println("group " + name + " not found");
//        assert false; // shouldn't happen
//        return null;
//    }
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

    private VisualizationGroupI selectedGroup = null;
    private ReplicateGroupI selectedReplicateGroup = null;

    @Override
    public void setSelectedVizGroup(VisualizationGroupI group) {
        if (group != null && selectedGroup != group && !group.isDeleted()) {
            selectedGroup = group;
            firePropertyChange(VISGROUP_SELECTION_CHANGED, 0, selectedGroup);
        }
    }

    @Override
    public final VisualizationGroupI getSelectedVisualizationGroup() {
        return selectedGroup;
    }

    @Override
    public <T> Future<T> submit(Fetcher<T> f) {
        return pool.submit(f);
    }

    @Override
    public Collection<ReplicateGroupI> getReplicateGroups() {
        return Collections.unmodifiableCollection(replicateGroups);
    }

    @Override
    public ReplicateGroupI createReplicateGroup() {
        ReplicateGroupI replGroup = null;

        synchronized (replicateGroups) {
            String newName = "Replicate Group " + replicateGroupCount;
            while (hasReplicateGroup(newName)) {
                replicateGroupCount++;
                newName = "Replicate Group " + replicateGroupCount;
            }
            Color groupColor = colors[(replicateGroupCount - 1) % colors.length];
            replGroup = new ReplicateGroup(newName);
            replGroup.setColor(groupColor);
            replicateGroups.add(replGroup);
            replGroup.addPropertyChangeListener(this);
        }

        firePropertyChange(REPLGROUP_ADDED, 0, replGroup);
        return replGroup;
    }

    @Override
    public void removeReplicateGroup(ReplicateGroupI rg) {
        for (ReplicateI r : rg.getReplicates()) {
            vizGroups.remove(r);
        }
        rg.close();
        replicateGroups.remove(rg);
        if (selectedReplicateGroup != null && selectedReplicateGroup.equals(rg)) {
            selectedReplicateGroup = null;
        }
        rg.deleted();
    }

    @Override
    public void setSelectedReplicateGroup(ReplicateGroupI replicateGroup) {
        if (selectedReplicateGroup != replicateGroup) {
            selectedReplicateGroup = replicateGroup;
            firePropertyChange(REPLICATEGROUP_SELECTION_CHANGED, 0, selectedReplicateGroup);
        }
    }
}
