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
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class VisualizationGroup {

    public static final String VISGROUP_ACTIVATED = "vgActivated";
    public static final String VISGROUP_DEACTIVATED = "vgDeactivated";
    public static final String VISGROUP_CHANGED = "vgChanged";
    public static final String VISGROUP_RENAMED = "vgRenamed";
    private String name;
    private Color color;
    private Set<SeqRun> seqruns = new HashSet<SeqRun>();
    private final Set<String> attributes = Collections.synchronizedSet(new HashSet<String>());
    private List<Thread> threads = new ArrayList<Thread>();
    private boolean is_active = true;
    private final PropertyChangeSupport pcs;

    public VisualizationGroup(String groupName) {
        this.name = groupName;
        pcs = new PropertyChangeSupport(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        pcs.firePropertyChange(VISGROUP_RENAMED, oldName, this.name);
    }

    public boolean isActive() {
        return is_active;
    }

    public void setActive(boolean is_active) {
        this.is_active = is_active;
        pcs.firePropertyChange(is_active ? VISGROUP_ACTIVATED : VISGROUP_DEACTIVATED, 0, getName());
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
        while (threads.size() > 0) {
            List<Thread> removeList = new ArrayList<Thread>();
            List<Thread> unfinished = new ArrayList<Thread>();
            for (Thread t : threads) {
                try {
                    t.join();
                    removeList.add(t);
                } catch (InterruptedException ex) {
                    unfinished.add(t);
                }
            }
            threads.removeAll(removeList);
            threads.addAll(unfinished);
        }
        return attributes;
    }

    private void prefetchAttributes(final SeqRun sr) {
        assert sr.getMaster() != null;
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    MGXMaster master = (MGXMaster) sr.getMaster();
                    Collection<String> types = master.Attribute().listTypesBySeqRun(sr.getId());
                    synchronized (attributes) {
                        attributes.addAll(types);
                    }
                    fireVGroupChanged(VISGROUP_CHANGED);
                } catch (MGXServerException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        Thread t = new Thread(r, "attribute-prefetch " + sr.getSequencingMethod());
        t.start();
        threads.add(t);
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