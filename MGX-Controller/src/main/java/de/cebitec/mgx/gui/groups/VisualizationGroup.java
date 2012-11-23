package de.cebitec.mgx.gui.groups;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
import java.awt.Color;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class VisualizationGroup {

    public static final String VISGROUP_ACTIVATED = "visgroup_activated";
    public static final String VISGROUP_DEACTIVATED = "visgroup_deactivated";
    public static final String VISGROUP_CHANGED = "visgroup_changed";
    public static final String VISGROUP_RENAMED = "visgroup_renamed";
    //
    private final int id;
    private String name;
    private Color color;
    private boolean is_active = true;
    private Set<SeqRun> seqruns = new HashSet<>();
    //
    private String selectedAttributeType;
    private Map<SeqRun, Job> uniqueJobs = new HashMap<>();
    private Map<SeqRun, List<Job>> needsResolval = new HashMap<>();
    //
    private final Map<SeqRun, Map<Job, List<AttributeType>>> attributeTypes = Collections.synchronizedMap(new HashMap<SeqRun, Map<Job, List<AttributeType>>>());
    private List<Fetcher> attributeTypePrefetchers = new ArrayList<>();
    private final PropertyChangeSupport pcs;
    //
    private Map<String, Distribution> distCache = new HashMap<>();
    private Map<String, Tree<Long>> hierarchyCache = new HashMap<>();

    public VisualizationGroup(int id, String groupName, Color color) {
        this.id = id;
        this.name = groupName;
        this.color = color;
        pcs = new PropertyChangeSupport(this);
    }

    public int getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
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

    /**
     *
     * @param attrType
     * @throws ConflictingJobsException
     *
     * promote selection of an attribute type to the group; checks all contained
     * sequencing runs, i.) if they provide the attribute type and ii.) if the
     * attribute type is provided by a single job only. if several jobs are able
     * to provide the corresponding attribute type, a ConflictingJobsException
     * will be raised for resolval of the conflict.
     */
    public final void selectAttributeType(String attrType) throws ConflictingJobsException {

        selectedAttributeType = attrType;
        uniqueJobs.clear();
        needsResolval.clear();

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
                    uniqueJobs.put(run, validJobs.get(0));
                    break;
                default:
                    needsResolval.put(run, validJobs);
                    break;
            }
        }

        if (!needsResolval.isEmpty()) {
            throw new ConflictingJobsException(this);
        }
    }

    public final Map<SeqRun, List<Job>> getConflicts() {
        return needsResolval;
    }

    public final void resolveConflict(SeqRun sr, Job j) {
        needsResolval.remove(sr);
        uniqueJobs.put(sr, j);
    }

    public final void addSeqRun(SeqRun sr) {
        if (seqruns.contains(sr)) {
            return;
        }
        distCache.clear(); // invalidate caches
        hierarchyCache.clear();

        seqruns.add(sr);
        sr.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.err.println("Vgroup got PCE " + evt.getPropertyName());
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
        });

        AttributeTypeFetcher fetcher = new AttributeTypeFetcher(sr, attributeTypes);
        fetcher.execute();
        attributeTypePrefetchers.add(fetcher);
    }

    public void removeSeqRun(SeqRun sr) {
        seqruns.remove(sr);
        distCache.clear(); // invalidate caches
        hierarchyCache.clear();
        fireVGroupChanged(VISGROUP_CHANGED);
    }

    public final Tree<Long> getHierarchy() {
        assert !EventQueue.isDispatchThread();
        assert selectedAttributeType != null;
        assert needsResolval.isEmpty();

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
            Job selectedJob = uniqueJobs.get(run);
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

    public final Distribution getDistribution() {

        assert !EventQueue.isDispatchThread();
        assert selectedAttributeType != null;
        assert needsResolval.isEmpty();

        if (distCache.containsKey(selectedAttributeType)) {
            return distCache.get(selectedAttributeType);
        }

        List<Distribution> results = Collections.synchronizedList(new ArrayList<Distribution>());

        // start distribution retrieval workers in background
        //
        CountDownLatch allDone = new CountDownLatch(seqruns.size());

        for (SeqRun run : seqruns) {

            Job selectedJob = uniqueJobs.get(run);
            //
            // there should only be one valid attribute type left that matches the
            // request attribute type name; however, we better check..
            //
            AttributeType currentAttributeType = selectAttributeType(run, selectedJob, selectedAttributeType);

            // 
            // start background worker to fetch distribution
            //
            if (selectedJob == null || selectedAttributeType == null) {
                allDone.countDown();
            } else {
                DistributionFetcher distFetcher = new DistributionFetcher(currentAttributeType, selectedJob, allDone, results);
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
        Distribution ret = mergeDistributions(results);
        distCache.put(selectedAttributeType, ret);

        return ret;
    }

    public final List<AttributeType> getAttributeTypes() {
        waitForWorkers(attributeTypePrefetchers);
        List<AttributeType> ret = new ArrayList<>();
        for (Map<Job, List<AttributeType>> l : attributeTypes.values()) {
            for (List<AttributeType> atypes : l.values()) {
                ret.addAll(atypes);
            }
        }
        return ret;
    }

    public List<Job> getJobsProvidingAttributeType(SeqRun run, String attrTypeName) {
        //
        // process all jobs for a seqrun and keep only those
        // which provide the requested attribute type
        //
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

    private static Distribution mergeDistributions(final Iterable<Distribution> dists) {
        Map<Attribute, Number> summary = new HashMap<>();
        long total = 0;
        MGXMasterI anyMaster = null;

        for (Distribution d : dists) {
            anyMaster = (MGXMaster) d.getMaster();
            total += d.getTotalClassifiedElements();
            for (Entry<Attribute, ? extends Number> e : d.entrySet()) {
                Attribute attr = e.getKey();
                long count = e.getValue().longValue();
                if (summary.containsKey(attr)) {
                    count += summary.get(attr).longValue();
                }
                summary.put(attr, count);
            }
        }

        return new Distribution(summary, total, anyMaster);
    }

    private void waitForWorkers(List<Fetcher> workerList) {
        List<Fetcher> removeList = new ArrayList<>();
        while (workerList.size() > 0) {
            removeList.clear();
            for (Fetcher sw : workerList) {
                if (sw.isDone()) {
                    removeList.add(sw);
                }
            }
            workerList.removeAll(removeList);
        }
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

    private class AttributeTypeFetcher extends Fetcher<Map<Job, List<AttributeType>>> {

        protected final SeqRun run;
        protected final Map<SeqRun, Map<Job, List<AttributeType>>> result;

        public AttributeTypeFetcher(final SeqRun run, final Map<SeqRun, Map<Job, List<AttributeType>>> result) {
            this.run = run;
            this.result = result;
        }

        @Override
        protected Map<Job, List<AttributeType>> doInBackground() throws Exception {
            MGXMaster master = (MGXMaster) run.getMaster();
            Map<Job, List<AttributeType>> ret = master.SeqRun().getJobsAndAttributeTypes(run.getId());
            return ret;
        }

        @Override
        protected void done() {
            Map<Job, List<AttributeType>> get = null;
            try {
                get = get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            result.put(run, get);
            fireVGroupChanged(VISGROUP_CHANGED);
            super.done();
        }
    }

    private class DistributionFetcher extends Fetcher<Distribution> {

        protected final AttributeType attrType;
        protected final Job job;
        protected final CountDownLatch latch;
        protected final List<Distribution> result;

        public DistributionFetcher(final AttributeType attrType, final Job job, final CountDownLatch latch, List<Distribution> ret) {
            this.attrType = attrType;
            this.job = job;
            this.latch = latch;
            this.result = ret;
        }

        @Override
        protected Distribution doInBackground() throws Exception {
            MGXMaster master = (MGXMaster) attrType.getMaster();
            return master.Attribute().getDistribution(attrType.getId(), job.getId());
        }

        @Override
        protected void done() {
            Distribution dist = null;
            try {
                dist = get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            }
            result.add(dist);
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
}