package de.cebitec.mgx.common;

import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class VisualizationGroup implements VisualizationGroupI {

    //public static final String VISGROUP_DESELECTED = "vgDeselected";
    //
    //
    private final VGroupManagerI vgmgr;
    private String name;
    private Color color;
    private boolean is_active = true;
    private String selectedAttributeType;
    private String secondaryAttributeType; // 2nd attr type for correlation matrices
    private final Map<AttributeRank, Map<SeqRunI, JobI>> uniqueJobs = new HashMap<>();
    private final Map<AttributeRank, Map<SeqRunI, Set<JobI>>> needsResolval = new HashMap<>();
    private final Map<SeqRunI, Map<JobI, Set<AttributeTypeI>>> attributeTypes;
    private final Map<SeqRunI, DistributionI<Long>> currentDistributions;
    //
    private final PropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this);
    //
    private final Map<String, DistributionI<Long>> distCache = new HashMap<>();
    private final Map<String, TreeI<Long>> hierarchyCache = new HashMap<>();
    //
    private final int id;
    private final UUID uuid = UUID.randomUUID();
    private final DataFlavor dataflavor;
    private String managedState;

    VisualizationGroup(VGroupManagerI vgmgr, int id, String groupName, Color color) {
        this(VisualizationGroupI.VISGROUP_DATA_FLAVOR, vgmgr, id, groupName, color);
    }

    VisualizationGroup(DataFlavor dataFlavor, VGroupManagerI vgmgr, int id, String groupName, Color color) {
        this.dataflavor = dataFlavor;
        this.id = id;
        this.vgmgr = vgmgr;
        this.name = groupName;
        this.color = color;
        uniqueJobs.put(AttributeRank.PRIMARY, new HashMap<SeqRunI, JobI>());
        uniqueJobs.put(AttributeRank.SECONDARY, new HashMap<SeqRunI, JobI>());
        needsResolval.put(AttributeRank.PRIMARY, new HashMap<SeqRunI, Set<JobI>>());
        needsResolval.put(AttributeRank.SECONDARY, new HashMap<SeqRunI, Set<JobI>>());
        attributeTypes = new ConcurrentHashMap<>();
        currentDistributions = new ConcurrentHashMap<>();
    }

    @Override
    public void close() {
        for (SeqRunI sr : getSeqRuns()) {
            sr.removePropertyChangeListener(this);
        }
        attributeTypes.clear();
        currentDistributions.clear();
    }

    @Override
    public final int getId() {
        return id;
    }

    @Override
    public final UUID getUUID() {
        return uuid;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        String oldVal = this.name;
        this.name = name;
        firePropertyChange(VISGROUP_RENAMED, oldVal, name);
    }

    @Override
    public boolean isActive() {
        return is_active && attributeTypes.size() > 0;
    }

    @Override
    public void setActive(boolean is_active) {
        this.is_active = is_active;
        pcs.firePropertyChange(is_active ? VISGROUP_ACTIVATED : VISGROUP_DEACTIVATED, !is_active, is_active);
    }

    @Override
    public final Color getColor() {
        return color;
    }

    @Override
    public final void setColor(Color color) {
        this.color = color;
    }

    @Override
    public final long getNumSequences() {
        long ret = 0;
        for (SeqRunI sr : getSeqRuns()) {
            ret += sr.getNumSequences();
        }
        return ret;
    }

    @Override
    public final Set<SeqRunI> getSeqRuns() {
        return attributeTypes.keySet();
    }

    @Override
    public final String getSelectedAttributeType() {
        return selectedAttributeType;
    }

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
    @Override
    public final void selectAttributeType(AttributeRank rank, String attrType) throws ConflictingJobsException {
        assert attrType != null;
        selectedAttributeType = attrType;
        synchronized (needsResolval) {
            uniqueJobs.get(rank).clear();
            needsResolval.get(rank).clear();
        }

        // clear caches - the cache might already contain data for the
        // selected attribute type, but from a different set of selected
        // jobs
        if (distCache.containsKey(attrType)) {
            distCache.remove(attrType);
        }
        if (hierarchyCache.containsKey(attrType)) {
            hierarchyCache.remove(attrType);
        }

        for (SeqRunI run : getSeqRuns()) {

            Set<JobI> validJobs = getJobsProvidingAttributeType(run, selectedAttributeType);
            //
            // select the job to use - either we can automatically determine
            // the correct job or we have to ask the user
            //
            switch (validJobs.size()) {
                case 0:
                    // nothing to do, no job provides this attribute type
                    break;
                case 1:
                    uniqueJobs.get(rank).put(run, validJobs.toArray(new JobI[]{})[0]);
                    break;
                default:
                    needsResolval.get(rank).put(run, validJobs);
                    break;
            }
        }

        if (!needsResolval.get(rank).isEmpty()) {
            throw new ConflictingJobsException(this, needsResolval.get(rank));
//        } else {
//            fireVGroupChanged(VISGROUP_ATTRTYPE_CHANGED);
        }
    }

    @Override
    public List<Triple<AttributeRank, SeqRunI, Set<JobI>>> getConflicts() {
        List<Triple<AttributeRank, SeqRunI, Set<JobI>>> ret = new ArrayList<>();
        for (Map.Entry<SeqRunI, Set<JobI>> e : getConflicts(AttributeRank.PRIMARY).entrySet()) {
            ret.add(new Triple<>(AttributeRank.PRIMARY, e.getKey(), e.getValue()));
        }
        for (Map.Entry<SeqRunI, Set<JobI>> e : getConflicts(AttributeRank.SECONDARY).entrySet()) {
            ret.add(new Triple<>(AttributeRank.SECONDARY, e.getKey(), e.getValue()));
        }
        return ret;
    }

    @Override
    public Map<SeqRunI, Set<JobI>> getConflicts(AttributeRank rank) {
        return needsResolval.get(rank);
    }

    @Override
    public final void resolveConflict(AttributeRank rank, SeqRunI sr, JobI j) {
        assert j != null;
        assert needsResolval.get(rank).containsKey(sr);
        synchronized (needsResolval) {
            Set<JobI> options = needsResolval.get(rank).remove(sr);
            assert options.contains(j);
            uniqueJobs.get(rank).put(sr, j);
        }

//        if (needsResolval.get(rank).isEmpty()) {
//            fireVGroupChanged(VISGROUP_ATTRTYPE_CHANGED);
//        }
    }

    @Override
    public final void addSeqRuns(final Set<SeqRunI> runs) {
        MultiAttributeTypeFetcher fetcher = new MultiAttributeTypeFetcher(runs);
        Future<Map<SeqRunI, Map<JobI, Set<AttributeTypeI>>>> f = vgmgr.submit(fetcher);

        try {
            Map<SeqRunI, Map<JobI, Set<AttributeTypeI>>> get = f.get();
            for (Map.Entry<SeqRunI, Map<JobI, Set<AttributeTypeI>>> e : get.entrySet()) {
                e.getKey().addPropertyChangeListener(this);
                attributeTypes.put(e.getKey(), e.getValue());

                // remove cached data for modified attribute types
                for (Set<AttributeTypeI> s : e.getValue().values()) {
                    for (AttributeTypeI at : s) {
                        distCache.remove(at.getName());
                        hierarchyCache.remove(at.getName());
                    }
                }
            }

        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }

        fireVGroupChanged(VISGROUP_CHANGED);
    }

    @Override
    public final void addSeqRun(final SeqRunI sr) {
        if (sr == null || attributeTypes.containsKey(sr)) {
            return;
        }

        AttributeTypeFetcher fetcher = new AttributeTypeFetcher(sr);
        Future<Map<JobI, Set<AttributeTypeI>>> f = vgmgr.submit(fetcher);

        try {
            Map<JobI, Set<AttributeTypeI>> get = f.get();
            sr.addPropertyChangeListener(this);
            attributeTypes.put(sr, get);

            // remove cached data for modified attribute types
            for (Set<AttributeTypeI> s : get.values()) {
                for (AttributeTypeI at : s) {
                    distCache.remove(at.getName());
                    hierarchyCache.remove(at.getName());
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }

        fireVGroupChanged(VISGROUP_CHANGED);
    }

    @Override
    public final void removeSeqRun(final SeqRunI sr) {
        if (!attributeTypes.containsKey(sr)) {
            return;
        }

        sr.removePropertyChangeListener(this);
        Map<JobI, Set<AttributeTypeI>> remove = attributeTypes.remove(sr);

        // remove cached data for modified attribute types
        for (Set<AttributeTypeI> s : remove.values()) {
            for (AttributeTypeI at : s) {
                distCache.remove(at.getName());
                hierarchyCache.remove(at.getName());
            }
        }

        fireVGroupChanged(VISGROUP_CHANGED);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case OBJECT_DELETED:
                if (evt.getSource() instanceof SeqRunI) {
                    removeSeqRun((SeqRunI) evt.getSource());
                }
                pcs.firePropertyChange(evt);
                break;
            case OBJECT_MODIFIED:
                pcs.firePropertyChange(evt);
                break;
            default:
                System.err.println("unhandled PCE: " + evt.getPropertyName());
                assert false;
        }
    }

    @Override
    public final TreeI<Long> getHierarchy() {
        assert selectedAttributeType != null;
        assert needsResolval.get(AttributeRank.PRIMARY).isEmpty();

        if (hierarchyCache.containsKey(selectedAttributeType)) {
            return hierarchyCache.get(selectedAttributeType);
        }

        // start distribution retrieval workers
        //
        List<Future<TreeI<Long>>> results = new ArrayList<>();

        for (SeqRunI run : getSeqRuns()) {
            //
            // select the job to use - either we can automatically determine
            // the correct job or we have to ask the user
            //
            JobI selectedJob = uniqueJobs.get(AttributeRank.PRIMARY).get(run);
            //
            // there should only be one valid attribute type left that matches the
            // request attribute type name; however, we better check..
            //
            AttributeTypeI currentAttributeType = selectAttributeType(run, selectedJob, selectedAttributeType);

            // 
            // start worker to fetch distribution
            //
            if (selectedJob != null && currentAttributeType != null) {
                HierarchyFetcher fetcher = new HierarchyFetcher(currentAttributeType, selectedJob);
                Future<TreeI<Long>> f = vgmgr.submit(fetcher);
                results.add(f);
            }
        }

        //
        // merge results
        //
        TreeI<Long> ret;
        try {
            ret = TreeFactory.mergeTrees(results);
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }

        hierarchyCache.put(selectedAttributeType, ret);
        return ret;
    }

    @Override
    public final DistributionI<Long> getDistribution() throws ConflictingJobsException {

        assert selectedAttributeType != null;
        //assert needsResolval.get(AttributeRank.PRIMARY).isEmpty();
        if (!needsResolval.get(AttributeRank.PRIMARY).isEmpty()) {
            throw new ConflictingJobsException(this, needsResolval.get(AttributeRank.PRIMARY));
        }

        if (distCache.containsKey(selectedAttributeType)) {
            return distCache.get(selectedAttributeType);
        }

        List<Future<Pair<SeqRunI, DistributionI<Long>>>> results = new ArrayList<>();

        // start distribution retrieval workers in background
        //
        for (SeqRunI run : getSeqRuns()) {

            JobI selectedJob = uniqueJobs.get(AttributeRank.PRIMARY).get(run);
            //
            // there should only be one valid attribute type left that matches the
            // request attribute type name; however, we better check..
            //
            AttributeTypeI currentAttributeType = selectAttributeType(run, selectedJob, selectedAttributeType);

            // 
            // start background worker to fetch distribution
            //
            if (selectedJob != null && currentAttributeType != null) {
                DistributionFetcher distFetcher = new DistributionFetcher(run, currentAttributeType, selectedJob); // currentDistributions);
                Future<Pair<SeqRunI, DistributionI<Long>>> f = vgmgr.submit(distFetcher);
                results.add(f);
//              distFetcher.execute();
            }
        }

        //
        // merge results
        //
        currentDistributions.clear();
        DistributionI<Long> ret;
        try {
            ret = DistributionFactory.merge(results, currentDistributions);
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
        distCache.put(selectedAttributeType, ret);
        fireVGroupChanged(VISGROUP_HAS_DIST);

        return ret;
    }

    @Override
    public final Iterator<AttributeTypeI> getAttributeTypes() {
        Set<AttributeTypeI> ret = new HashSet<>();

        for (Map<JobI, Set<AttributeTypeI>> l : attributeTypes.values()) {
            for (Set<AttributeTypeI> atypes : l.values()) {
                ret.addAll(atypes);
            }
        }
        return ret.iterator();
    }

    private Set<JobI> getJobsProvidingAttributeType(SeqRunI run, final String attrTypeName) {
        //
        // process all jobs for a seqrun and keep only those
        // which provide the requested attribute type
        //
        assert attributeTypes.containsKey(run);
        assert attributeTypes.get(run) != null;

        Set<JobI> validJobs = new HashSet<>();
        for (Entry<JobI, Set<AttributeTypeI>> entrySet : attributeTypes.get(run).entrySet()) {
            for (AttributeTypeI atype : entrySet.getValue()) {
                if (atype.getName().equals(attrTypeName)) {
                    validJobs.add(entrySet.getKey());
                    break;
                }
            }
        }
        return validJobs;
    }

    private AttributeTypeI selectAttributeType(SeqRunI run, JobI job, String attrTypeName) {
        if (job == null) {
            // no job for this run provides the selected attribute type
            return null;
        }
        List<AttributeTypeI> validTypes = new ArrayList<>();
        Map<JobI, Set<AttributeTypeI>> jobattrtypes = attributeTypes.get(run);
        Set<AttributeTypeI> attributesForJob = jobattrtypes.get(job);
        if (attributesForJob == null) {
            Logger.getGlobal().log(Level.SEVERE, "no attributes for run {0} from job {1}", new Object[]{run.getName(), job.getId()});
        }
        for (AttributeTypeI atype : attributesForJob) {
            if (atype.getName().equals(attrTypeName)) {
                validTypes.add(atype);
            }
        }
        assert validTypes.size() == 1; // shouldn't happen
        return validTypes.get(0);
    }

    @Override
    public Map<SeqRunI, Set<AttributeI>> getSaveSet(List<String> requestedAttrs) {
        assert needsResolval.get(AttributeRank.PRIMARY).isEmpty();
        Map<SeqRunI, Set<AttributeI>> filtered = new HashMap<>();
        for (Entry<SeqRunI, DistributionI<Long>> e : currentDistributions.entrySet()) {

            Set<AttributeI> relevant = new HashSet<>();
            for (AttributeI a : e.getValue().keySet()) {
                if (requestedAttrs.contains(a.getValue())) {
                    relevant.add(a);
                }
            }

            if (!relevant.isEmpty()) {
                filtered.put(e.getKey(), relevant);
            }
        }
        return filtered;
    }

    private void fireVGroupChanged(String name) {
        pcs.firePropertyChange(name, 0, getName());
    }

//    private int getNumberOfAttributeTypes() {
//        // get current number of attribute types
//        int curAttrTypeCnt = 0;
//        Iterator<AttributeTypeI> it = getAttributeTypes();
//        while (it.hasNext()) {
//            it.next();
//            curAttrTypeCnt++;
//        }
//        return curAttrTypeCnt;
//    }
    @Override
    public void modified() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot modify deleted object.");
        }
        firePropertyChange(OBJECT_MODIFIED, 1, 2);
    }

    @Override
    public void deleted() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot delete deleted object.");
        }
        firePropertyChange(OBJECT_DELETED, 0, 1);
        managedState = OBJECT_DELETED;
    }

//    @Override
//    public int compareTo(VisualizationGroupI o) {
//        return Integer.compare(getId(), o.getId());
//    }

    @Override
    public final DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{dataflavor};
    }

    @Override
    public final boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor != null && flavor.equals(dataflavor);
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
        firePropertyChange(evt);
    }

    @Override
    public final void firePropertyChange(String propertyName, int oldValue, int newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
        //pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        firePropertyChange(evt);
    }

    @Override
    public final void firePropertyChange(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }

    @Override
    public int compareTo(VisualizationGroupI o) {
        return Integer.compare(getId(), o.getId());
    }
}
