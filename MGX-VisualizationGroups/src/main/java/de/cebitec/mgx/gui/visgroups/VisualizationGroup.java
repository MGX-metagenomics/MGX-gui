package de.cebitec.mgx.gui.visgroups;

import de.cebitec.mgx.gui.visgroups.workers.ReadBasedAttributeTypeFetcher;
import de.cebitec.mgx.gui.visgroups.workers.MultiAttributeTypeFetcher;
import de.cebitec.mgx.gui.visgroups.workers.HierarchyFetcher;
import de.cebitec.mgx.gui.visgroups.workers.DistributionFetcher;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.ConflictingJobsForSeqRunException;
import de.cebitec.mgx.api.groups.GroupI;
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
import de.cebitec.mgx.gui.datafactories.DistributionFactory;
import de.cebitec.mgx.gui.datafactories.TreeFactory;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class VisualizationGroup implements VisualizationGroupI {

    //private final static Map<JobI, Set<AttributeTypeI>> NO_JOBS = new HashMap<>();
    //
    private final VGroupManagerI vgmgr;
    private String name;
    private Color color;
    private boolean is_active = true;
    //
    private String selectedAttributeType;
    private String secondaryAttributeType; // 2nd attr type for correlation matrices
    private final Map<AttributeRank, Map<SeqRunI, JobI>> uniqueJobs = new ConcurrentHashMap<>();
    private final Map<AttributeRank, Map<SeqRunI, Set<JobI>>> needsResolval = new ConcurrentHashMap<>();
    private final ConcurrentMap<SeqRunI, Map<JobI, Set<AttributeTypeI>>> attributeTypes;
    private final Map<SeqRunI, DistributionI<Long>> currentDistributions;
    //
    private final Map<String, DistributionI<Long>> distCache = new ConcurrentHashMap<>();
    private final Map<String, TreeI<Long>> hierarchyCache = new ConcurrentHashMap<>();
    //
    //
    private final int id;
    private final UUID uuid = UUID.randomUUID();
    private final ParallelPropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this);
    //
    private String managedState = OBJECT_MANAGED;

    VisualizationGroup(VGroupManagerI vgmgr, int id, String groupName, Color color) {
        this.id = id;
        this.vgmgr = vgmgr;
        this.name = groupName;
        this.color = color;
        uniqueJobs.put(AttributeRank.PRIMARY, new ConcurrentHashMap<SeqRunI, JobI>());
        uniqueJobs.put(AttributeRank.SECONDARY, new ConcurrentHashMap<SeqRunI, JobI>());
        needsResolval.put(AttributeRank.PRIMARY, new ConcurrentHashMap<SeqRunI, Set<JobI>>());
        needsResolval.put(AttributeRank.SECONDARY, new ConcurrentHashMap<SeqRunI, Set<JobI>>());
        attributeTypes = new ConcurrentHashMap<>();
        currentDistributions = new ConcurrentHashMap<>();
    }

    @Override
    public VGroupManagerI getManager() {
        return vgmgr;
    }

    @Override
    public synchronized void close() {
        for (SeqRunI sr : attributeTypes.keySet()) {
            sr.removePropertyChangeListener(this);
        }
        attributeTypes.clear();
        currentDistributions.clear();
        uniqueJobs.get(AttributeRank.PRIMARY).clear();
        uniqueJobs.get(AttributeRank.SECONDARY).clear();
        needsResolval.get(AttributeRank.PRIMARY).clear();
        needsResolval.get(AttributeRank.SECONDARY).clear();
        distCache.clear();
        hierarchyCache.clear();
        deleted();
        pcs.close();
    }

    @Override
    public Class<SeqRunI> getContentClass() {
        return SeqRunI.class;
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
    public String getDisplayName() {
        return getName();
    }

    @Override
    public void setName(String newName) {
        if (!name.equals(newName)) {
            String oldVal = this.name;
            this.name = newName;
            firePropertyChange(VISGROUP_RENAMED, oldVal, name);
        }
    }

    @Override
    public boolean isActive() {
        return is_active && !isDeleted(); // && !attributeTypes.isEmpty();
    }

    @Override
    public void setActive(boolean is_active) {
        this.is_active = is_active;
        if (!is_active) {
            selectedAttributeType = null;
            uniqueJobs.get(AttributeRank.PRIMARY).clear();
            uniqueJobs.get(AttributeRank.SECONDARY).clear();
            needsResolval.get(AttributeRank.PRIMARY).clear();
            needsResolval.get(AttributeRank.SECONDARY).clear();
        }
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
        for (SeqRunI sr : getContent()) {
            ret += sr.getNumSequences();
        }
        return ret;
    }

    @Override
    public int getNumberOfSeqRuns() {
        return getContent().size();
    }

    @Override
    public List<SeqRunI> getSeqRuns() {
        // TODO convert to immutable list
        return List.copyOf(getContent());
    }

    @Override
    public final Set<SeqRunI> getContent() {
        return Collections.unmodifiableSet(attributeTypes.keySet());
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
        if (attrType == null || attrType.isEmpty()) {
            return;
        }

        if (attrType.equals(getSelectedAttributeType())) {
            return;
        }

        if (!rank.equals(AttributeRank.PRIMARY)) {
            // everything else is unsupported so far
            return;
        }

        synchronized (needsResolval) {
            selectedAttributeType = attrType;
            uniqueJobs.get(rank).clear();
            needsResolval.get(rank).clear();
        }

        // clear caches - the cache might already contain data for the
        // selected attribute type, but from a different set of selected
        // jobs
        if (distCache.containsKey(selectedAttributeType)) {
            distCache.remove(selectedAttributeType);
        }
        if (hierarchyCache.containsKey(selectedAttributeType)) {
            hierarchyCache.remove(selectedAttributeType);
        }

        for (SeqRunI run : getContent()) {

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
                    synchronized (needsResolval) {
                        needsResolval.get(rank).put(run, validJobs);
                    }
                    break;
            }
        }

        if (!needsResolval.get(rank).isEmpty()) {
            selectedAttributeType = null;
            throw new ConflictingJobsForSeqRunException(this, needsResolval.get(rank));
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
    public final void resolveConflict(AttributeRank rank, String attributeType, SeqRunI sr, JobI j) {
        assert j != null;
        assert needsResolval.get(rank).containsKey(sr);
        synchronized (needsResolval) {
            Set<JobI> options = needsResolval.get(rank).remove(sr);
            assert options.contains(j);
            uniqueJobs.get(rank).put(sr, j);
        }

        if (needsResolval.get(rank).isEmpty()) {
            selectedAttributeType = attributeType;
        }
    }

    @Override
    public final void add(final SeqRunI... runs) {
        if (!isActive() || runs.length == 0) {
            return;
        }

        for (SeqRunI run : runs) {
            if (run.isDeleted()) {
                return;
                //throw new IllegalArgumentException(run.getName() + " is already marked as deleted.");
            }
            run.addPropertyChangeListener(this);

//            attributeTypes.put(run, NO_JOBS);
        }

        MultiAttributeTypeFetcher fetcher = new MultiAttributeTypeFetcher(runs);
        Future<Map<SeqRunI, Map<JobI, Set<AttributeTypeI>>>> f = vgmgr.submit(fetcher);

        try {
            Map<SeqRunI, Map<JobI, Set<AttributeTypeI>>> get = f.get();
            for (Map.Entry<SeqRunI, Map<JobI, Set<AttributeTypeI>>> entry : get.entrySet()) {
                SeqRunI run = entry.getKey();
                Map<JobI, Set<AttributeTypeI>> jobData = entry.getValue();

                for (JobI job : jobData.keySet()) {
                    job.addPropertyChangeListener(this);
                }

                attributeTypes.put(run, jobData);

                // remove cached data for modified attribute types
                for (Set<AttributeTypeI> s : jobData.values()) {
                    for (AttributeTypeI attrType : s) {
                        distCache.remove(attrType.getName());
                        hierarchyCache.remove(attrType.getName());
                    }
                }
            }

        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }

        for (SeqRunI run : runs) {

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
                    uniqueJobs.get(AttributeRank.PRIMARY).put(run, validJobs.toArray(new JobI[]{})[0]);
                    break;
                default:
                    synchronized (needsResolval) {
                        needsResolval.get(AttributeRank.PRIMARY).put(run, validJobs);
                    }
                    break;
            }
        }

        // if unresolved conflicts remain, reset the currently selected attribute type,
        // so the next invocation of selectAttributeType(sameAttrType) will lead to 
        // conflict resolution instead of returning previously cached data
        if (!needsResolval.get(AttributeRank.PRIMARY).isEmpty()) {
            selectedAttributeType = null;
        }

        fireVGroupChanged(VISGROUP_CHANGED);
    }

    @Override
    public final void add(final SeqRunI sr) {
        if (isDeleted() || sr == null || attributeTypes.containsKey(sr) || sr.isDeleted()) {
            return;
        }
//        if (sr.isDeleted()) {
//            throw new IllegalArgumentException(sr.getName() + " is already marked as deleted.");
//        }
        sr.addPropertyChangeListener(this);

        //attributeTypes.put(sr, NO_JOBS);
        ReadBasedAttributeTypeFetcher fetcher = new ReadBasedAttributeTypeFetcher(sr);
        Future<Map<JobI, Set<AttributeTypeI>>> f = vgmgr.submit(fetcher);

        try {
            Map<JobI, Set<AttributeTypeI>> get = f.get();

            for (JobI job : get.keySet()) {
                job.addPropertyChangeListener(this);
            }

            attributeTypes.put(sr, get);

            // remove cached data for modified attribute types
            for (Set<AttributeTypeI> s : get.values()) {
                for (AttributeTypeI attrType : s) {
                    distCache.remove(attrType.getName());
                    hierarchyCache.remove(attrType.getName());
                }
            }
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }

        Set<JobI> validJobs = getJobsProvidingAttributeType(sr, selectedAttributeType);
        //
        // select the job to use - either we can automatically determine
        // the correct job or we have to ask the user
        //
        switch (validJobs.size()) {
            case 0:
                // nothing to do, no job provides this attribute type
                break;
            case 1:
                uniqueJobs.get(AttributeRank.PRIMARY).put(sr, validJobs.toArray(new JobI[]{})[0]);
                break;
            default:
                synchronized (needsResolval) {
                    needsResolval.get(AttributeRank.PRIMARY).put(sr, validJobs);
                }
                break;
        }

        // if unresolved conflicts remain, reset the currently selected attribute type,
        // so the next invocation of selectAttributeType(sameAttrType) will lead to 
        // conflict resolution instead of returning previously cached data
        if (!needsResolval.get(AttributeRank.PRIMARY).isEmpty()) {
            selectedAttributeType = null;
        }

        fireVGroupChanged(VISGROUP_CHANGED);
        //modified();
    }

    @Override
    public final void remove(final SeqRunI sr) {
        if (!attributeTypes.containsKey(sr)) {
            return;
        }
        sr.removePropertyChangeListener(this);
        Map<JobI, Set<AttributeTypeI>> remove = attributeTypes.remove(sr);
        currentDistributions.remove(sr);

        // remove cached data for modified attribute types
        for (Map.Entry<JobI, Set<AttributeTypeI>> me : remove.entrySet()) {
            me.getKey().removePropertyChangeListener(this);
            Set<AttributeTypeI> s = me.getValue();
            for (AttributeTypeI at : s) {
                distCache.remove(at.getName());
                hierarchyCache.remove(at.getName());
            }
        }

        fireVGroupChanged(VISGROUP_CHANGED);
        //modified();
    }

    @Override
    public final TreeI<Long> getHierarchy() throws ConflictingJobsException {

        assert isActive();

        // create a local copy of the attribute type in case the attribute type
        // selection is changed on another thread
        String copyOfSelectedAttributeType = selectedAttributeType;

        if (copyOfSelectedAttributeType == null) {
            System.err.println("VGroup " + getDisplayName() + ": attribute type is null");
            return null;
        }

        if (!needsResolval.get(AttributeRank.PRIMARY).isEmpty()) {
            throw new ConflictingJobsForSeqRunException(this, needsResolval.get(AttributeRank.PRIMARY));
        }

        if (hierarchyCache.containsKey(copyOfSelectedAttributeType)) {
            return hierarchyCache.get(copyOfSelectedAttributeType);
        }

        // start distribution retrieval workers
        //
        List<Future<TreeI<Long>>> results = new ArrayList<>();

        for (SeqRunI run : getContent()) {
            //
            // select the job to use - either we can automatically determine
            // the correct job or we have to ask the user
            //
            JobI selectedJob = uniqueJobs.get(AttributeRank.PRIMARY).get(run);
            //
            // there should only be one valid attribute type left that matches the
            // request attribute type name; however, we better check..
            //
            AttributeTypeI currentAttributeType = selectAttributeType(run, selectedJob, copyOfSelectedAttributeType);

            // 
            // start worker to fetch distribution
            //
            if (selectedJob != null && currentAttributeType != null) {
                assert !selectedJob.isDeleted();
                HierarchyFetcher fetcher = new HierarchyFetcher(run, currentAttributeType, selectedJob);
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
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        hierarchyCache.put(copyOfSelectedAttributeType, ret);
        return ret;
    }

    @Override
    public final DistributionI<Long> getDistribution() throws ConflictingJobsException {

        assert isActive();

        // create a local copy of the attribute type in case the attribute type
        // selection is changed on another thread
        String copyOfSelectedAttributeType = selectedAttributeType;

        if (copyOfSelectedAttributeType == null) {
            System.err.println("VGroup " + getDisplayName() + ": attribute type is null");
            return null;
        }
        if (!needsResolval.get(AttributeRank.PRIMARY).isEmpty()) {
            throw new ConflictingJobsForSeqRunException(this, needsResolval.get(AttributeRank.PRIMARY));
        }

        if (distCache.containsKey(copyOfSelectedAttributeType)) {
            return distCache.get(copyOfSelectedAttributeType);
        }

        List<Future<Pair<SeqRunI, DistributionI<Long>>>> results = new ArrayList<>();

        // start distribution retrieval workers in background
        //
        for (SeqRunI run : getContent()) {

            JobI selectedJob = uniqueJobs.get(AttributeRank.PRIMARY).get(run);
            //
            // there should only be one valid attribute type left that matches the
            // request attribute type name; however, we better check..
            //
            AttributeTypeI currentAttributeType = selectAttributeType(run, selectedJob, copyOfSelectedAttributeType);

            // 
            // start background worker to fetch distribution
            //
            if (selectedJob != null && currentAttributeType != null) {
                DistributionFetcher distFetcher = new DistributionFetcher(run, currentAttributeType, selectedJob); // currentDistributions);
                Future<Pair<SeqRunI, DistributionI<Long>>> f = vgmgr.submit(distFetcher);
                results.add(f);
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
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        assert ret != null;
        distCache.put(copyOfSelectedAttributeType, ret);
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

        assert !isDeleted();
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
        } else {
            for (AttributeTypeI atype : attributesForJob) {
                if (atype.getName().equals(attrTypeName)) {
                    validTypes.add(atype);
                }
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case OBJECT_DELETED:
                if (evt.getSource() instanceof SeqRunI) {
                    remove((SeqRunI) evt.getSource());
                }
                if (evt.getSource() instanceof JobI) {
                    JobI job = (JobI) evt.getSource();
                    SeqRunI[] runs = job.getSeqruns();
                    for (SeqRunI seqrun : runs) {
                        // refresh data for this run
                        remove(seqrun);
                        add(seqrun);
                    }
                }
                //pcs.firePropertyChange(evt);
                break;
            case OBJECT_MODIFIED:
                pcs.firePropertyChange(evt);
                break;
            default:
                System.err.println("unhandled PCE in VGroup: " + evt.getPropertyName());
                assert false;
        }
    }

    @Override
    public final synchronized void modified() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot modify deleted object.");
        }
        firePropertyChange(OBJECT_MODIFIED, 1, 2);
    }

    @Override
    public final void childChanged() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state for " + getClass().getSimpleName() + ", cannot modify deleted object.");
        }
        firePropertyChange(CHILD_CHANGE, 1, 2);
    }

    @Override
    public final synchronized void deleted() {
        if (managedState.equals(OBJECT_DELETED)) {
            throw new RuntimeException("Invalid object state, cannot delete deleted object.");
        }
        firePropertyChange(OBJECT_DELETED, 0, 1);
        managedState = OBJECT_DELETED;
    }

    @Override
    public final boolean isDeleted() {
        return managedState.equals(OBJECT_DELETED);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{VisualizationGroupI.VISGROUP_DATA_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor != null && flavor.equals(VisualizationGroupI.VISGROUP_DATA_FLAVOR);
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
        pcs.firePropertyChange(evt);
    }

    @Override
    public final void firePropertyChange(String propertyName, int oldValue, int newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        pcs.firePropertyChange(evt);
    }

    @Override
    public final void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
        pcs.firePropertyChange(evt);
    }

    protected final void firePropertyChange(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }

    @Override
    public int compareTo(GroupI<SeqRunI> o) {
        return Integer.compare(getId(), o.getId());
    }
}
