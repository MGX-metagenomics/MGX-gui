package de.cebitec.mgx.gui.groups;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.datamodel.misc.AttributeRank;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.DistributionFactory;
import de.cebitec.mgx.gui.datamodel.misc.Triple;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
import java.awt.Color;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class VisualizationGroup implements PropertyChangeListener {

    public static final String VISGROUP_ACTIVATED = "visgroup_activated";
    public static final String VISGROUP_DEACTIVATED = "visgroup_deactivated";
    public static final String VISGROUP_CHANGED = "visgroup_changed";
    public static final String VISGROUP_RENAMED = "visgroup_renamed";
    public static final String VISGROUP_ATTRTYPE_CHANGED = "vgAttrTypeChange";
    public static final String VISGROUP_HAS_DIST = "vgHasDist";
    //public static final String VISGROUP_DESELECTED = "vgDeselected";
    //
    //
    private final int id;
    private String name;
    private Color color;
    private boolean is_active = true;
    private final Set<SeqRun> seqruns = new HashSet<>();
    //
    private String selectedAttributeType;
    private String secondaryAttributeType; // 2nd attr type for correlation matrices
    private final Map<AttributeRank, Map<SeqRun, Job>> uniqueJobs = new HashMap<>();
    private final Map<AttributeRank, Map<SeqRun, List<Job>>> needsResolval = new HashMap<>();
    //
    private final BlockingQueue<AttributeTypeFetcher> fetcherQueue = new LinkedBlockingQueue<>();
    private final Thread fetchThread;
    //
    private final Map<SeqRun, Map<Job, List<AttributeType>>> attributeTypes;
    private final Map<SeqRun, Distribution> currentDistributions;
    //
    //private List<Fetcher> attributeTypePrefetchers = new ArrayList<>();
    private final PropertyChangeSupport pcs;
    //
    private final Map<String, Distribution> distCache = new HashMap<>();
    private final Map<String, Tree<Long>> hierarchyCache = new HashMap<>();

    VisualizationGroup(int id, String groupName, Color color) {
        this.id = id;
        this.name = groupName;
        this.color = color;
        uniqueJobs.put(AttributeRank.PRIMARY, new HashMap<SeqRun, Job>());
        uniqueJobs.put(AttributeRank.SECONDARY, new HashMap<SeqRun, Job>());
        needsResolval.put(AttributeRank.PRIMARY, new HashMap<SeqRun, List<Job>>());
        needsResolval.put(AttributeRank.SECONDARY, new HashMap<SeqRun, List<Job>>());
        pcs = new PropertyChangeSupport(this);
        attributeTypes = new ConcurrentHashMap<>();
        currentDistributions = new ConcurrentHashMap<>();
        fetchThread = new Thread(new QHandler(fetcherQueue));
        fetchThread.setName("VisualizationGroup-" + id + "-AttributeType Manager");
        fetchThread.start();
    }

    protected void close() {
        fetchThread.interrupt();
        try {
            fetchThread.join();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public int getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        String oldVal = this.name;
        this.name = name;
        pcs.firePropertyChange(VISGROUP_RENAMED, oldVal, name);
    }

    public final boolean isActive() {
        return is_active && seqruns.size() > 0;
    }

    public final void setActive(boolean is_active) {
        this.is_active = is_active;
        pcs.firePropertyChange(is_active ? VISGROUP_ACTIVATED : VISGROUP_DEACTIVATED, !is_active, is_active);
    }

    public final Color getColor() {
        return color;
    }

    public final void setColor(Color color) {
        this.color = color;
    }

    public final long getNumSequences() {
        long ret = 0;
        for (SeqRun sr : seqruns) {
            ret += sr.getNumSequences();
        }
        return ret;
    }

    public final Set<SeqRun> getSeqRuns() {
        return seqruns;
    }

    public final String getSelectedAttributeType() {
        return selectedAttributeType;
    }

//    public final void selectAttributeType(String attrType) throws ConflictingJobsException {
//        selectAttributeType(AttributeRank.PRIMARY, attrType);
//    }
//
//    public final void selectSecondaryAttributeType(String attrType) throws ConflictingJobsException {
//        selectAttributeType(AttributeRank.SECONDARY, attrType);
//    }
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

        for (SeqRun run : seqruns) {

            List<Job> validJobs = getJobsProvidingAttributeType(run, selectedAttributeType);
            //
            // select the job to use - either we can automatically determine
            // the correct job or we have to ask the user
            //
            switch (validJobs.size()) {
                case 0:
                    // nothing to do, no job provides this attribute type
                    break;
                case 1:
                    uniqueJobs.get(rank).put(run, validJobs.get(0));
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

    public List<Triple<AttributeRank, SeqRun, List<Job>>> getConflicts() {
        List<Triple<AttributeRank, SeqRun, List<Job>>> ret = new LinkedList<>();
        for (Map.Entry<SeqRun, List<Job>> e : getConflicts(AttributeRank.PRIMARY).entrySet()) {
            ret.add(new Triple<>(AttributeRank.PRIMARY, e.getKey(), e.getValue()));
        }
        for (Map.Entry<SeqRun, List<Job>> e : getConflicts(AttributeRank.SECONDARY).entrySet()) {
            ret.add(new Triple<>(AttributeRank.SECONDARY, e.getKey(), e.getValue()));
        }
        return ret;
    }

    Map<SeqRun, List<Job>> getConflicts(AttributeRank rank) {
        return needsResolval.get(rank);
    }

    public final void resolveConflict(AttributeRank rank, SeqRun sr, Job j) {
        assert j != null;
        assert needsResolval.get(rank).containsKey(sr);
        synchronized (needsResolval) {
            List<Job> options = needsResolval.get(rank).remove(sr);
            assert options.contains(j);
            uniqueJobs.get(rank).put(sr, j);
        }

//        if (needsResolval.get(rank).isEmpty()) {
//            fireVGroupChanged(VISGROUP_ATTRTYPE_CHANGED);
//        }
    }

    public final void addSeqRun(SeqRun sr) {
        if (seqruns.contains(sr)) {
            return;
        }
        distCache.clear(); // invalidate caches
        hierarchyCache.clear();

        //seqruns.add(sr);
        sr.addPropertyChangeListener(this);

        CountDownLatch l = new CountDownLatch(1);

        AttributeTypeFetcher fetcher = new AttributeTypeFetcher(sr, l);
        fetcher.execute();
        fetcherQueue.add(fetcher);
        try {
            l.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        // fetcher thread will fire change event once task is complete
        // fireVGroupChanged(VISGROUP_CHANGED);

    }

    public void removeSeqRun(SeqRun sr) {
        synchronized (seqruns) {
            sr.removePropertyChangeListener(this);
            seqruns.remove(sr);
            distCache.clear(); // invalidate caches
            hierarchyCache.clear();
            attributeTypes.remove(sr);
        }
        fireVGroupChanged(VISGROUP_CHANGED);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ModelBase.OBJECT_DELETED:
                removeSeqRun((SeqRun) evt.getOldValue());
                pcs.firePropertyChange(evt);
                break;
            default:
                System.err.println("unhandled PCE: " + evt.getPropertyName());
                assert false;
        }
    }

    public final Tree<Long> getHierarchy() {
        assert !EventQueue.isDispatchThread();
        assert selectedAttributeType != null;
        assert needsResolval.get(AttributeRank.PRIMARY).isEmpty();

        if (hierarchyCache.containsKey(selectedAttributeType)) {
            return hierarchyCache.get(selectedAttributeType);
        }

        List<Tree<Long>> results = Collections.synchronizedList(new ArrayList<Tree<Long>>());

        // start distribution retrieval workers in background
        //
        CountDownLatch allDone = new CountDownLatch(seqruns.size());

        for (SeqRun run : seqruns) {
            //
            // select the job to use - either we can automatically determine
            // the correct job or we have to ask the user
            //
            Job selectedJob = uniqueJobs.get(AttributeRank.PRIMARY).get(run);
            //
            // there should only be one valid attribute type left that matches the
            // request attribute type name; however, we better check..
            //
            AttributeType currentAttributeType = selectAttributeType(run, selectedJob, selectedAttributeType);

            // 
            // start background worker to fetch distribution
            //
            if (selectedJob == null || currentAttributeType == null) {
                allDone.countDown();
            } else {
                HierarchyFetcher fetcher = new HierarchyFetcher(currentAttributeType, selectedJob, allDone, results);
                fetcher.execute();
            }
        }

        // wait for completion of workers
        //
        try {
            allDone.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        //
        // merge results
        //

        Tree<Long> ret = TreeFactory.mergeTrees(results);

        hierarchyCache.put(selectedAttributeType, ret);
        return ret;
    }

    public final Distribution getDistribution() throws ConflictingJobsException {

        assert !EventQueue.isDispatchThread();
        assert selectedAttributeType != null;
        //assert needsResolval.get(AttributeRank.PRIMARY).isEmpty();
        if (!needsResolval.get(AttributeRank.PRIMARY).isEmpty()) {
            throw new ConflictingJobsException(this, needsResolval.get(AttributeRank.PRIMARY));
        }

        if (distCache.containsKey(selectedAttributeType)) {
            return distCache.get(selectedAttributeType);
        }

        currentDistributions.clear();
        //List<Distribution> results = Collections.synchronizedList(new ArrayList<Distribution>());

        // start distribution retrieval workers in background
        //
        CountDownLatch allDone = new CountDownLatch(seqruns.size());

        for (SeqRun run : seqruns) {

            Job selectedJob = uniqueJobs.get(AttributeRank.PRIMARY).get(run);
            //
            // there should only be one valid attribute type left that matches the
            // request attribute type name; however, we better check..
            //
            AttributeType currentAttributeType = selectAttributeType(run, selectedJob, selectedAttributeType);

            // 
            // start background worker to fetch distribution
            //
            if (selectedJob == null || currentAttributeType == null) {
                allDone.countDown();
            } else {
                DistributionFetcher distFetcher = new DistributionFetcher(run, currentAttributeType, selectedJob, allDone, currentDistributions);
                distFetcher.execute();
            }
        }

        // wait for completion of workers
        //
        try {
            allDone.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        //
        // merge results
        //
        Distribution ret = DistributionFactory.merge(currentDistributions.values());
        distCache.put(selectedAttributeType, ret);
        fireVGroupChanged(VISGROUP_HAS_DIST);

        return ret;
    }

    public final List<AttributeType> getAttributeTypes() {

        List<AttributeType> ret = new ArrayList<>();
        synchronized (seqruns) {
            assert attributeTypes.keySet().size() == seqruns.size();

            for (Map<Job, List<AttributeType>> l : attributeTypes.values()) {
                for (List<AttributeType> atypes : l.values()) {
                    ret.addAll(atypes);
                }
            }
        }
        return ret;
    }

    private List<Job> getJobsProvidingAttributeType(SeqRun run, final String attrTypeName) {
        //
        // process all jobs for a seqrun and keep only those
        // which provide the requested attribute type
        //
        assert attributeTypes.containsKey(run);
        assert attributeTypes.get(run) != null;

        List<Job> validJobs = new ArrayList<>();
        for (Entry<Job, List<AttributeType>> entrySet : attributeTypes.get(run).entrySet()) {
            for (AttributeType atype : entrySet.getValue()) {
                if (atype.getName().equals(attrTypeName)) {
                    validJobs.add(entrySet.getKey());
                    break;
                }
            }
        }
        return validJobs;
    }

    private AttributeType selectAttributeType(SeqRun run, Job job, String attrTypeName) {
        if (job == null) {
            // no job for this run provides the selected attribute type
            return null;
        }
        List<AttributeType> validTypes = new ArrayList<>();
        Map<Job, List<AttributeType>> jobattrtypes = attributeTypes.get(run);
        List<AttributeType> attributesForJob = jobattrtypes.get(job);
        if (attributesForJob == null) {
            Logger.getGlobal().log(Level.SEVERE, "no attributes for run {0} from job {1}", new Object[]{run.getName(), job.getId()});
        }
        for (AttributeType atype : attributesForJob) {
            if (atype.getName().equals(attrTypeName)) {
                validTypes.add(atype);
            }
        }
        assert validTypes.size() == 1; // shouldn't happen
        return validTypes.get(0);
    }

//    private static Distribution mergeDistributions(final Iterable<Distribution> dists) {
//        Map<Attribute, Number> summary = new HashMap<>();
//        long total = 0;
//        MGXMasterI anyMaster = null;
//
//        for (Distribution d : dists) {
//            anyMaster = (MGXMaster) d.getMaster();
//            total += d.getTotalClassifiedElements();
//            for (Entry<Attribute, ? extends Number> e : d.entrySet()) {
//                Attribute attr = e.getKey();
//                long count = e.getValue().longValue();
//                if (summary.containsKey(attr)) {
//                    count += summary.get(attr).longValue();
//                }
//                summary.put(attr, count);
//            }
//        }
//
//        return new Distribution(summary, total, anyMaster);
//    }
    public Map<SeqRun, Set<Attribute>> getSaveSet(List<String> requestedAttrs) {
        assert needsResolval.get(AttributeRank.PRIMARY).isEmpty();
        Map<SeqRun, Set<Attribute>> filtered = new HashMap<>();
        for (Entry<SeqRun, Distribution> e : currentDistributions.entrySet()) {

            Set<Attribute> relevant = new HashSet<>();
            for (Attribute a : e.getValue().keySet()) {
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

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }

    public abstract class Fetcher<T> extends SwingWorker<T, Void> {
    }

    private final class AttributeTypeFetcher extends Fetcher<Map<Job, List<AttributeType>>> {

        private final SeqRun run;
        private final CountDownLatch latch;

        public AttributeTypeFetcher(final SeqRun run, final CountDownLatch l) {
            this.run = run;
            this.latch = l;
        }

        public SeqRun getRun() {
            return run;
        }

        public void processed() {
            latch.countDown();
        }

        @Override
        protected Map<Job, List<AttributeType>> doInBackground() throws Exception {
            MGXMaster master = (MGXMaster) run.getMaster();
            Map<Job, List<AttributeType>> ret = master.SeqRun().getJobsAndAttributeTypes(run);
//            latch.countDown();
            return ret;
        }

    }

    private class DistributionFetcher extends Fetcher<Distribution> {

        protected final SeqRun run;
        protected final AttributeType attrType;
        protected final Job job;
        protected final CountDownLatch latch;
        protected final Map<SeqRun, Distribution> result;

        public DistributionFetcher(final SeqRun run, final AttributeType attrType, final Job job, final CountDownLatch latch, Map<SeqRun, Distribution> ret) {
            this.run = run;
            this.attrType = attrType;
            this.job = job;
            this.latch = latch;
            this.result = ret;
            assert result != null;
        }

        @Override
        protected Distribution doInBackground() throws Exception {
            MGXMaster master = (MGXMaster) attrType.getMaster();
            if (attrType.getStructure() == AttributeType.STRUCTURE_HIERARCHICAL) {
                Tree<Long> tree = master.Attribute().getHierarchy(attrType.getId(), job.getId());
                return DistributionFactory.fromTree(tree, attrType);
            } else {
                return master.Attribute().getDistribution(attrType.getId(), job.getId());
            }
        }

        @Override
        protected void done() {
            Distribution dist = null;
            try {
                dist = get();
                result.put(run, dist);
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            latch.countDown();
        }
    }

    private final class HierarchyFetcher extends Fetcher<Tree<Long>> {

        protected final AttributeType attrType;
        protected final Job job;
        protected final CountDownLatch latch;
        protected final List<Tree<Long>> result;

        public HierarchyFetcher(final AttributeType attrType, final Job job, final CountDownLatch latch, final List<Tree<Long>> ret) {
            this.attrType = attrType;
            this.job = job;
            this.latch = latch;
            this.result = ret;
        }

        @Override
        protected Tree<Long> doInBackground() throws Exception {
            MGXMaster master = (MGXMaster) attrType.getMaster();
            return master.Attribute().getHierarchy(attrType.getId(), job.getId());
        }

        @Override
        protected void done() {
            Tree<Long> tree = null;
            try {
                tree = get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            result.add(tree);
            latch.countDown();
        }
    }

    private final class QHandler implements Runnable {

        private final BlockingQueue<AttributeTypeFetcher> queue;

        public QHandler(BlockingQueue<AttributeTypeFetcher> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    AttributeTypeFetcher fetcher = queue.take();
                    try {
                        Map<Job, List<AttributeType>> ret = fetcher.get();
                        synchronized (seqruns) {
                            SeqRun newRun = fetcher.getRun();
                            // make sure seqrun hasn't been removed from this group
                            //if (seqruns.contains(fetcher.getRun())) {
                            seqruns.add(newRun);
                            attributeTypes.put(fetcher.getRun(), ret);
                            //}
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        fetcher.processed();
                    }

                    if (queue.isEmpty()) {
                        fireVGroupChanged(VISGROUP_CHANGED);
                    }
                }
            } catch (InterruptedException ex) {
            }
        }
    }
}
