package de.cebitec.mgx.common;

import de.cebitec.mgx.api.groups.ConflictingJobsException;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    private static VGroupManagerI instance = null;

    // LinkedHashSet keeps the order elements are added
    private final Map<Integer, VisualizationGroupI> groups = new LinkedHashMap<>();
    private int groupCount = 1;
    private final ParallelPropertyChangeSupport pcs;
    final RequestProcessor pool;

    private final Map<AttributeRank, String> currentAttributeType = new HashMap<>();
    private ConflictResolver resolver = null;
    //
    private static final Color colors[] = {Color.RED, Color.BLUE, Color.YELLOW, Color.PINK, Color.GREEN};

    private VGroupManager() {
        pcs = new ParallelPropertyChangeSupport(this);
        pool = new RequestProcessor("VGroupTasks", Runtime.getRuntime().availableProcessors() + 3);
    }

    public synchronized static VGroupManagerI getInstance() {
        if (instance == null) {
            instance = new VGroupManager();
        }
        return instance;
    }

    @Override
    public void registerResolver(ConflictResolver cr) {
        resolver = cr;
    }

    @Override
    public boolean hasGroup(String name) {
        for (VisualizationGroupI vg : groups.values()) {
            if (name.equals(vg.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<VisualizationGroupI> getActiveGroups() {
        List<VisualizationGroupI> ret = new ArrayList<>();
        for (VisualizationGroupI g : groups.values()) {
            if (g.isActive()) {
                ret.add(g);
            }
        }
        return ret;
    }

    @Override
    public Collection<VisualizationGroupI> getAllGroups() {
        return groups.values();
    }

    @Override
    public boolean selectAttributeType(AttributeRank rank, String aType) {
        if (aType == null) {
            return false;
        }

        assert resolver != null;

        if (!aType.equals(currentAttributeType.get(rank))) {
            List<VisualizationGroupI> conflicts = new ArrayList<>();
            for (VisualizationGroupI vg : getActiveGroups()) {
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
        List<Pair<VisualizationGroupI, DistributionI<Long>>> ret = new ArrayList<>(getActiveGroups().size());
        for (VisualizationGroupI vg : getActiveGroups()) {
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
        for (VisualizationGroupI vg : getActiveGroups()) {
            Map<SeqRunI, Set<JobI>> conflicts = vg.getConflicts(AttributeRank.PRIMARY);
            if (!conflicts.isEmpty()) {
                return null;
            }
        }

        List<Pair<VisualizationGroupI, TreeI<Long>>> ret = new ArrayList<>();
        for (VisualizationGroupI vg : getActiveGroups()) {
            TreeI<Long> tree = vg.getHierarchy();
            if (tree != null && !tree.isEmpty()) {
                ret.add(new Pair<>(vg, tree));
            }
        }
        return ret;
    }

    @Override
    public VisualizationGroupI createGroup() {
        String newName = "Group " + groupCount;
        while (hasGroup(newName)) {
            newName = "Group " + ++groupCount;
        }
        Color groupColor = colors[(groupCount - 1) % colors.length];
        VisualizationGroupI group = new VisualizationGroup(this, groupCount, newName, groupColor);
        group.addPropertyChangeListener(this);
        groups.put(groupCount, group);
        groupCount++;
        //firePropertyChange(VISGROUP_NUM_CHANGED, 0, newName);
        return group;
    }

    @Override
    public void removeGroup(VisualizationGroupI vg) {
        synchronized (groups) {
            vg.close();
            groups.remove(vg.getId());
        }
        firePropertyChange(VISGROUP_NUM_CHANGED, 0, vg.getName());
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        // rename group
//        if (pce.getPropertyName().equals(VisualizationGroup.VISGROUP_RENAMED)) {
//            VisualizationGroup vg = getGroup((String) pce.getOldValue());
//            if (vg != null) {
//                vg.setName((String) pce.getNewValue());
//            }
//        }
        firePropertyChange(pce.getPropertyName(), pce.getOldValue(), pce.getNewValue());
    }

    private VisualizationGroupI getGroup(String name) {
        for (VisualizationGroupI vg : groups.values()) {
            if (name.equals(vg.getName())) {
                return vg;
            }
        }
        //System.err.println("group " + name + " not found");
        assert false; // shouldn't happen
        return null;
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

    private VisualizationGroupI selectedGroup = null;

    @Override
    public void setSelectedGroup(VisualizationGroupI group) {
        selectedGroup = group;
        firePropertyChange(VISGROUP_SELECTION_CHANGED, 0, selectedGroup);
    }

    @Override
    public VisualizationGroupI getSelectedGroup() {
        return selectedGroup;
    }
    
    @Override
    public <T> Future<T> submit(Fetcher<T> f) {
        return pool.submit(f);
    }
}
