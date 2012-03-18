package de.cebitec.mgx.gui.attributevisualization.data;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.*;
import java.awt.Color;
import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map.Entry;
import java.util.*;
import java.util.concurrent.CountDownLatch;
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

    public final Distribution getHierarchy(String attrTypeName) {
        assert !EventQueue.isDispatchThread();
        return null;
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
        System.err.println("starting " + seqruns.size() + " dist fetchers");

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
        System.err.println("waiting for dist fetchers");
        try {
            allDone.await();
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        }
        System.err.println("dist fetchers done");
        //
        // merge results
        //
        Distribution ret = mergeDistributions(results);
        distCache.put(attrTypeName, ret);

        return ret;
    }

    public final List<AttributeType> getAttributeTypes() {
        System.err.println("vgroup getAttributeTypes");
        waitForWorkers(attributeTypePrefetchers);
        List<AttributeType> ret = new ArrayList<AttributeType>();
        for (Map<Job, List<AttributeType>> l : attributeTypes.values()) {
            for (List<AttributeType> atypes : l.values()) {
                ret.addAll(atypes);
            }
        }
        System.err.println("vgroup has attrtypes "+ ret.toString());
        return ret;
    }

    private final Job selectJob(SeqRun run, String attrTypeName) {
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

    private abstract class Fetcher extends SwingWorker<Void, Void> {
    }

    private class AttributeTypeFetcher extends Fetcher {

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

    private class DistributionFetcher extends Fetcher {

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
            System.err.println("distfetcher beginning work..");
            MGXMaster master = (MGXMaster) attrType.getMaster();
            Map<Attribute, Long> dist = master.Attribute().getDistribution(attrType, job);
            result.add(dist);
            System.err.println("distfetcher done with work..");
            return null;
        }

        @Override
        protected void done() {
            //super.done();
            System.err.println("distfetcher done, decreasing latch");
            latch.countDown();
        }
    }

    private final class HierarchyFetcher extends DistributionFetcher {

        public HierarchyFetcher(AttributeType attrType, Job job, CountDownLatch latch, List<Map<Attribute, ? extends Number>> ret) {
            super(attrType, job, latch, ret);
        }

        @Override
        protected Void doInBackground() throws Exception {
            MGXMaster master = (MGXMaster) attrType.getMaster();
            Map<Attribute, Long> dist = master.Attribute().getHierarchy(attrType, job);
            result.add(dist);
            return null;
        }
    }
}