package de.cebitec.mgx.gui.attributevisualization.data;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.datamodel.tree.Node;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
import java.awt.Color;
import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map.Entry;
import java.util.*;
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

    public static final String VISGROUP_ACTIVATED = "vgActivated";
    public static final String VISGROUP_DEACTIVATED = "vgDeactivated";
    public static final String VISGROUP_CHANGED = "vgModified";
    public static final String VISGROUP_RENAMED = "vgRenamed";
    //
    private String name;
    private Color color;
    private boolean is_active = true;
    private Set<SeqRun> seqruns = new HashSet<SeqRun>();
    //
    private final Map<SeqRun, Map<Job, List<AttributeType>>> attributeTypes = Collections.synchronizedMap(new HashMap<SeqRun, Map<Job, List<AttributeType>>>());
    private List<Fetcher> attributeTypePrefetchers = new ArrayList<Fetcher>();
    private final PropertyChangeSupport pcs;
    //
    private Map<String, Distribution> distCache = new HashMap<String, Distribution>();
    private Map<String, Tree<Long>> hierarchyCache = new HashMap<String, Tree<Long>>();

    public VisualizationGroup(String groupName, Color color) {
        this.name = groupName;
        this.color = color;
        pcs = new PropertyChangeSupport(this);
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        String oldName = this.name;
        this.name = name;
        pcs.firePropertyChange(VISGROUP_RENAMED, oldName, name);
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
        fireVGroupChanged(VISGROUP_CHANGED);
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

    public final void addSeqRun(SeqRun sr) {
        if (!seqruns.contains(sr)) {
            distCache.clear(); // invalidate cache
            seqruns.add(sr);
            AttributeTypeFetcher fetcher = new AttributeTypeFetcher(sr);
            fetcher.execute();
            attributeTypePrefetchers.add(fetcher);
        }
    }

    public final Tree<Long> getHierarchy(String attrTypeName) {
        assert !EventQueue.isDispatchThread();

        if (hierarchyCache.containsKey(attrTypeName)) {
            return hierarchyCache.get(attrTypeName);
        }

        List<Tree<Long>> results = Collections.synchronizedList(new ArrayList<Tree<Long>>());
        int numExpectedTrees = 0;


        // start distribution retrieval workers in background
        //
        CountDownLatch allDone = new CountDownLatch(seqruns.size());

        for (SeqRun run : seqruns) {
            //
            // select the job to use - either we can automatically determine
            // the correct job or we have to ask the user
            //
            Job selectedJob = selectJob(run, attrTypeName);

            //
            // there should only be one valid attribute type left that matches the
            // request attribute type name; however, we better check..
            //
            AttributeType selectedAttributeType = selectAttributeType(run, selectedJob, attrTypeName);

            // 
            // start background worker to fetch distribution
            //
            if (selectedJob == null || selectedAttributeType == null) {
                allDone.countDown();
            } else {
                HierarchyFetcher fetcher = new HierarchyFetcher(selectedAttributeType, selectedJob, allDone, results);
                numExpectedTrees++;
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
        
        hierarchyCache.put(attrTypeName, ret);
        return ret;
    }

    public final Distribution getDistribution(String attrTypeName) {

        assert !EventQueue.isDispatchThread();

        if (distCache.containsKey(attrTypeName)) {
            return distCache.get(attrTypeName);
        }

        List<Map<Attribute, ? extends Number>> results = Collections.synchronizedList(new ArrayList<Map<Attribute, ? extends Number>>());

        // start distribution retrieval workers in background
        //
        CountDownLatch allDone = new CountDownLatch(seqruns.size());

        for (SeqRun run : seqruns) {
            //
            // select the job to use - either we can automatically determine
            // the correct job or we have to ask the user
            //
            Job selectedJob = selectJob(run, attrTypeName);

            //
            // there should only be one valid attribute type left that matches the
            // request attribute type name; however, we better check..
            //
            AttributeType selectedAttributeType = selectAttributeType(run, selectedJob, attrTypeName);

            // 
            // start background worker to fetch distribution
            //
            if (selectedJob == null || selectedAttributeType == null) {
                allDone.countDown();
            } else {
                DistributionFetcher distFetcher = new DistributionFetcher(selectedAttributeType, selectedJob, allDone, results);
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
        distCache.put(attrTypeName, ret);
        
        return ret;
    }

    public final List<AttributeType> getAttributeTypes() {
        waitForWorkers(attributeTypePrefetchers);
        List<AttributeType> ret = new ArrayList<AttributeType>();
        for (Map<Job, List<AttributeType>> l : attributeTypes.values()) {
            for (List<AttributeType> atypes : l.values()) {
                ret.addAll(atypes);
            }
        }
        return ret;
    }

    private Job selectJob(SeqRun run, String attrTypeName) {
        //
        // process all jobs for this seqrun and keep only those
        // which provide the requested attribute type
        //
        List<Job> validJobs = new ArrayList<Job>();
        for (Entry<Job, List<AttributeType>> entrySet : attributeTypes.get(run).entrySet()) {
            for (AttributeType atype : entrySet.getValue()) {
                if (atype.getName().equals(attrTypeName)) {
                    validJobs.add(entrySet.getKey());
                    break;
                }
            }
        }

        //
        // select the job to use - either we can automatically determine
        // the correct job or we have to ask the user
        //
        Job selectedJob = null;
        switch (validJobs.size()) {
            case 0:
                // nothing to do, no job provides this attribute type
                break;
            case 1:
                selectedJob = validJobs.get(0);
                break;
            default:
                selectedJob = askUser(validJobs);
                break;
        }

        return selectedJob;
    }

    private AttributeType selectAttributeType(SeqRun run, Job job, String attrTypeName) {
        List<AttributeType> validTypes = new ArrayList<AttributeType>();
        for (AttributeType atype : attributeTypes.get(run).get(job)) {
            if (atype.getName().equals(attrTypeName)) {
                validTypes.add(atype);
            }
        }
        assert validTypes.size() == 1;
        return validTypes.get(0);
    }

    private static Distribution mergeDistributions(List<Map<Attribute, ? extends Number>> dists) {
        Map<Attribute, Long> summary = new HashMap<Attribute, Long>();
        for (Map<Attribute, ? extends Number> d : dists) {
            for (Entry<Attribute, ? extends Number> e : d.entrySet()) {
                Attribute attr = e.getKey();
                Long count = e.getValue().longValue();
                if (summary.containsKey(attr)) {
                    count += summary.get(attr);
                }
                summary.put(attr, count);
            }
        }

        return new Distribution(summary);
    }

    private static Job askUser(List<Job> jobs) {
        // FIXME
        return jobs.get(0);
    }

    private void waitForWorkers(List<Fetcher> workerList) {
        List<Fetcher> removeList = new ArrayList<Fetcher>();
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

    private abstract class Fetcher<T> extends SwingWorker<T, Void> {
    }

    private class AttributeTypeFetcher extends Fetcher<Void> {

        protected SeqRun run;

        public AttributeTypeFetcher(SeqRun run) {
            this.run = run;
        }

        @Override
        protected Void doInBackground() throws Exception {
            MGXMaster master = (MGXMaster) run.getMaster();
            Map<Job, List<AttributeType>> attrTypes = new HashMap<Job, List<AttributeType>>();
            for (Job job : master.Job().BySeqRun(run)) {
                attrTypes.put(job, master.AttributeType().ByJob(job));
            }
            attributeTypes.put(run, attrTypes);
            return null;
        }

        @Override
        protected void done() {
            fireVGroupChanged(VISGROUP_CHANGED);
            super.done();
        }
    }

    private class DistributionFetcher extends Fetcher<Void> {

        protected AttributeType attrType;
        protected Job job;
        protected CountDownLatch latch;
        protected final List<Map<Attribute, ? extends Number>> result;

        public DistributionFetcher(AttributeType attrType, Job job, CountDownLatch latch, List<Map<Attribute, ? extends Number>> ret) {
            this.attrType = attrType;
            this.job = job;
            this.latch = latch;
            this.result = ret;
        }

        @Override
        protected Void doInBackground() throws Exception {
            MGXMaster master = (MGXMaster) attrType.getMaster();
            Map<Attribute, Long> dist = master.Attribute().getDistribution(attrType, job);
            result.add(dist);
            return null;
        }

        @Override
        protected void done() {
            //super.done();
            latch.countDown();
        }
    }

    private final class HierarchyFetcher extends Fetcher<Tree<Long>> {

        protected AttributeType attrType;
        protected Job job;
        protected CountDownLatch latch;
        protected final List<Tree<Long>> result;

        public HierarchyFetcher(AttributeType attrType, Job job, CountDownLatch latch, List<Tree<Long>> ret) {
            this.attrType = attrType;
            this.job = job;
            this.latch = latch;
            this.result = ret;
        }

        @Override
        protected Tree<Long> doInBackground() throws Exception {
            MGXMaster master = (MGXMaster) attrType.getMaster();
            Tree<Long> tree = master.Attribute().getHierarchy(attrType, job);
            
            assert tree != null;
            Node<Long> root = tree.getRoot();
            assert root != null;
            
            return tree;
        }

        
        @Override
        protected void done() {
            Tree<Long> get = null;
            try {
                get = get();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            Logger.getLogger("HFetcher").log(Level.INFO, "   hierarchy fetched");
            assert get != null;
            result.add(get);
            latch.countDown();
        }
    }
}