package de.cebitec.mgx.gui.attributevisualization.data;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private String name;
    private Color color;
    private Set<SeqRun> seqruns = new HashSet<SeqRun>();
    private final Set<String> attributes = Collections.synchronizedSet(new HashSet<String>());
    private List<SwingWorker> workers = new ArrayList<SwingWorker>();
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
        return is_active;
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

    public Set<String> getAttributes() {
        while (workers.size() > 0) {
            List<SwingWorker> removeList = new ArrayList<SwingWorker>();
            for (SwingWorker sw : workers) {
                if (sw.isDone()) {
                    removeList.add(sw);
                }
            }
            workers.removeAll(removeList);
        }
        return attributes;
    }

    private void prefetchAttributes(final SeqRun sr) {
        assert sr.getMaster() != null;

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
        workers.add(sw);
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