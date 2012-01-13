package de.cebitec.mgx.gui.attributevisualization.data;

import de.cebitec.mgx.gui.attributevisualization.Pair;
import de.cebitec.mgx.gui.datamodel.Attribute;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sj
 */
public class VGroupManager implements PropertyChangeListener {

    public static final String VISGROUP_NUM_CHANGED = "vgNumChanged";
    private static VGroupManager instance = null;
    private List<VisualizationGroup> groups = new ArrayList<VisualizationGroup>();
    private int groupCount = 1;
    private PropertyChangeSupport pcs = null;
    //
    private static Color colors[] = {Color.RED, Color.BLUE, Color.YELLOW, Color.PINK, Color.GREEN};

    private VGroupManager() {
        pcs = new PropertyChangeSupport(this);
    }

    public static VGroupManager getInstance() {
        if (instance == null) {
            instance = new VGroupManager();
        }
        return instance;
    }

    public boolean hasGroup(String name) {
        for (VisualizationGroup vg : groups) {
            if (name.equals(vg.getName())) {
                return true;
            }
        }
        return false;
    }

    public List<VisualizationGroup> getGroups() {
        List<VisualizationGroup> ret = new ArrayList<VisualizationGroup>();
        for (VisualizationGroup g : groups) {
            if (g.isActive()) {
                ret.add(g);
            }
        }
        return ret;
    }

    public List<Pair<VisualizationGroup, Distribution>> getDistributions(String attrName) {
        List<Pair<VisualizationGroup, Distribution>> ret = new ArrayList<Pair<VisualizationGroup, Distribution>>();
        for (VisualizationGroup vg : getGroups()) {
            Map<Attribute, Number> dist = vg.getDistribution(attrName);
            if (!dist.isEmpty()) {
                ret.add(new Pair<VisualizationGroup, Distribution>(vg, new Distribution(dist)));
            }
        }
        return ret;
    }

    public VisualizationGroup createGroup() {
        String newName = "Group " + groupCount;
        while (hasGroup(newName)) {
            newName = "Group " + ++groupCount;
        }
        Color groupColor = colors[(groupCount - 1) % colors.length];
        VisualizationGroup group = new VisualizationGroup(newName, groupColor);
        group.addPropertyChangeListener(this);
        groups.add(group);
        groupCount++;
        firePropertyChange(VISGROUP_NUM_CHANGED, 0, newName);
        return group;
    }

    public void removeGroup(String name) {
        VisualizationGroup vg = getGroup(name);
        if (vg == null) {
            return;
        }
        groups.remove(vg);
        firePropertyChange(VISGROUP_NUM_CHANGED, 0, vg.getName());
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        // rename group
        if (pce.getPropertyName().equals(VisualizationGroup.VISGROUP_RENAMED)) {
            System.err.println("fetching vizGroup " + (String) pce.getOldValue());
            VisualizationGroup vg = getGroup((String) pce.getOldValue());
            if (vg != null) {
                System.err.println("renaming group twice?");
                vg.setName((String) pce.getNewValue());
            }
        }
        firePropertyChange(pce.getPropertyName(), pce.getOldValue(), pce.getNewValue());
    }

    private VisualizationGroup getGroup(String name) {
        for (VisualizationGroup vg : groups) {
            if (name.equals(vg.getName())) {
                return vg;
            }
        }

        return null;
    }

    private void firePropertyChange(String name, Object oldValue, Object newValue) {
        pcs.firePropertyChange(name, oldValue, newValue);
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        pcs.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        pcs.removePropertyChangeListener(p);
    }
}
