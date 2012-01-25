package de.cebitec.mgx.gui.attributevisualization.data;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.SwingWorker;

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
    private Set<SeqRun> seqruns = new HashSet<SeqRun>();
    private final Map<SeqRun, Map<Job, List<AttributeType>>> attributeTypes = Collections.synchronizedMap(new HashMap<SeqRun, Map<Job, List<AttributeType>>>());
    private List<SwingWorker> attributeTypePrefetchers = new ArrayList<SwingWorker>();
    private boolean is_active = true;
    private final PropertyChangeSupport pcs;

    public VisualizationGroup(String groupName, Color color) {
        this.name = groupName;
        this.color = color;
        pcs = new PropertyChangeSupport(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        pcs.firePropertyChange(VISGROUP_RENAMED, oldName, name);
    }

    public boolean isActive() {
        return is_active && seqruns.size() > 0;
    }

    public void setActive(boolean is_active) {
        this.is_active = is_active;
        pcs.firePropertyChange(is_active ? VISGROUP_ACTIVATED : VISGROUP_DEACTIVATED, !is_active, is_active);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        fireVGroupChanged(VISGROUP_CHANGED);
    }

    public Set<SeqRun> getSeqRuns() {
        return seqruns;
    }

    public void addSeqRun(SeqRun sr) {
        if (!seqruns.contains(sr)) {
            seqruns.add(sr);
            SwingWorker prefetcher = prefetchJobsAndAttributeTypes(sr);
            attributeTypePrefetchers.add(prefetcher);
        }
    }

    public Map<Attribute, Long> getDistribution(String attrTypeName) {
        List<Map<Attribute, Long>> results = Collections.synchronizedList(new ArrayList<Map<Attribute, Long>>());

        // start distribution retrieval workers in background
        //
        List<SwingWorker> distFetchers = new ArrayList<SwingWorker>();

        for (SeqRun run : getSeqRuns()) {

            //
            // process all jobs for this seqrun and keep only those
            // which provide the requested attribute type
            //
            List<Job> validJobs = new ArrayList<Job>();
            for (Entry<Job, List<AttributeType>> entrySet : attributeTypes.get(run).entrySet()) {
                boolean job_provides_this_attribute = false;
                for (AttributeType atype : entrySet.getValue()) {
                    if (atype.getName().equals(attrTypeName)) {
                        job_provides_this_attribute = true;
                    }
                }
                if (job_provides_this_attribute) {
                    validJobs.add(entrySet.getKey());
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
                    //SwingWorker sw = fetchDistributionInBackground(matchingAttrType, validJobs.get(0), ret);
                    //distFetchers.add(sw);
                    break;
                default:
                    selectedJob = askUser(validJobs);
                    //SwingWorker sw1 = fetchDistributionInBackground(matchingAttrType, userSelectedJob, ret);
                    //distFetchers.add(sw1);
                    break;
            }

            //
            // there should only be one valid attribute type left that matches the
            // request attribute type name; however, we better check..
            //
            AttributeType selectedAttributeType = null;
            List<AttributeType> validTypes = new ArrayList<AttributeType>();
            for (AttributeType atype : attributeTypes.get(run).get(selectedJob)) {
                if (atype.getName().equals(attrTypeName)) {
                    validTypes.add(atype);
                }
            }
            assert validTypes.size() == 1;
            selectedAttributeType = validTypes.get(0);

            // 
            // start background worker to fetch distribution
            //
            SwingWorker worker = fetchDistributionInBackground(selectedAttributeType, selectedJob, results);
            distFetchers.add(worker);
        }

        // wait for completion of workers
        //
        waitForWorkers(distFetchers);

        //
        // merge results
        //
        Map<Attribute, Long> ret = new HashMap<Attribute, Long>();
        for (Map<Attribute, Long> d : results) {
            for (Entry<Attribute, Long> e : d.entrySet()) {
                Attribute attr = e.getKey();
                Long count = e.getValue();
                if (ret.containsKey(attr)) {
                    count += ret.get(attr);
                }
                ret.put(attr, count);
            }
        }
        return ret;
    }

    public List<AttributeType> getAttributeTypes() {
        waitForWorkers(attributeTypePrefetchers);
        List<AttributeType> ret = new ArrayList<AttributeType>();
        for (Map<Job, List<AttributeType>> l : attributeTypes.values()) {
            for (List<AttributeType> atypes : l.values()) {
                ret.addAll(atypes);
            }
        }
        return ret;
    }

    private SwingWorker prefetchJobsAndAttributeTypes(final SeqRun sr) {

        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                MGXMaster master = (MGXMaster) sr.getMaster();
                Map<Job, List<AttributeType>> attrTypes = new HashMap<Job, List<AttributeType>>();
                for (Job job : master.Job().BySeqRun(sr)) {
                    attrTypes.put(job, master.AttributeType().ByJob(job));
                }
                attributeTypes.put(sr, attrTypes);
                return null;
            }

            @Override
            protected void done() {
                super.done();
                fireVGroupChanged(VISGROUP_CHANGED);
            }
        };
        sw.execute();

        return sw;
    }

    private SwingWorker fetchDistributionInBackground(final AttributeType aType, final Job job, final List<Map<Attribute, Long>> ret) {

        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                MGXMaster master = (MGXMaster) aType.getMaster();
                Map<Attribute, Long> dist = master.Attribute().getDistribution(aType, job);
                ret.add(dist);
                return null;
            }

            @Override
            protected void done() {
                super.done();
            }
        };
        sw.execute();
        return sw;
    }

    private Job askUser(List<Job> jobs) {
        // FIXME
        return jobs.get(0);
    }

    private void waitForWorkers(List<SwingWorker> workerList) {
        while (workerList.size() > 0) {
            List<SwingWorker> removeList = new ArrayList<SwingWorker>();
            for (SwingWorker sw : workerList) {
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
}