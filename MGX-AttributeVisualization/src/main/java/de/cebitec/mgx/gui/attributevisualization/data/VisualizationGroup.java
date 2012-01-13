package de.cebitec.mgx.gui.attributevisualization.data;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
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
    private String name;
    private Color color;
    private Set<SeqRun> seqruns = new HashSet<SeqRun>();
    private final Set<String> attributes = Collections.synchronizedSet(new HashSet<String>());
    private List<SwingWorker> attributePrefetchers = new ArrayList<SwingWorker>();
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
            prefetchAttributes(sr);
        }
    }

    public Map<Attribute, Number> getDistribution(String attrName) {
        Map<Attribute, Number> ret = Collections.synchronizedMap(new HashMap<Attribute, Number>());

        // we pre-group the seqrun based on their MGXMaster, i.e. based on project,
        // since we can then merge them into a single fetchDistribution() request
        Map<MGXMaster, List<Long>> fetchGroups = new HashMap<MGXMaster, List<Long>>();
        for (SeqRun sr : getSeqRuns()) {
            MGXMaster master = (MGXMaster) sr.getMaster();
            if (fetchGroups.containsKey(master)) {
                List<Long> srList = fetchGroups.get(master);
                srList.add(sr.getId());
            } else {
                List<Long> srList = new ArrayList<Long>();
                srList.add(sr.getId());
                fetchGroups.put(master, srList);
            }
        }

        // start distribution retrieval workers in background
        //
        List<SwingWorker> distFetchers = new ArrayList<SwingWorker>();
        for (Entry<MGXMaster, List<Long>> entry : fetchGroups.entrySet()) {
            MGXMaster master = entry.getKey();
            List<Long> runIdList = entry.getValue();
            SwingWorker sw = fetchDistributionInBackground(master, runIdList, attrName, ret);
            distFetchers.add(sw);
        }


        // wait for completion of workers
        //
        while (distFetchers.size() > 0) {
            List<SwingWorker> removeList = new ArrayList<SwingWorker>();
            for (SwingWorker sw : distFetchers) {
                if (sw.isDone()) {
                    removeList.add(sw);
                }
            }
            distFetchers.removeAll(removeList);
        }

        return ret;
    }

    public Set<String> getAttributes() {
        while (attributePrefetchers.size() > 0) {
            List<SwingWorker> removeList = new ArrayList<SwingWorker>();
            for (SwingWorker sw : attributePrefetchers) {
                if (sw.isDone()) {
                    removeList.add(sw);
                }
            }
            attributePrefetchers.removeAll(removeList);
        }
        return attributes;
    }

    private void prefetchAttributes(final SeqRun sr) {

        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                MGXMaster master = (MGXMaster) sr.getMaster();
                Collection<String> types = master.Attribute().listTypesBySeqRun(sr.getId());
                synchronized (attributes) {
                    attributes.addAll(types);
                }
                return null;
            }

            @Override
            protected void done() {
                super.done();
                fireVGroupChanged(VISGROUP_CHANGED);
            }
        };
        sw.execute();
        attributePrefetchers.add(sw);
    }

    private SwingWorker fetchDistributionInBackground(final MGXMaster master, final List<Long> runs, final String attr, final Map<Attribute, Number> ret) {

        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                Map<Attribute, Long> dist = master.Attribute().getDistributionByRuns(attr, runs);
                synchronized (ret) {
                    for (Attribute a : dist.keySet()) {
                        ret.put(a, dist.get(a));
                    }
                }
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