package de.cebitec.mgx.gui.attributevisualization;

import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sj
 */
public class VGroupManager implements PropertyChangeListener {

    public static final String VISGROUP_NUM_CHANGED = "vgNumChanged";
    private static VGroupManager instance = null;
    // LinkedHashSet keeps the order elements are added
    private Map<Integer, VisualizationGroup> groups = new LinkedHashMap<Integer, VisualizationGroup>();
    private int groupCount = 1;
    private PropertyChangeSupport pcs = null;
    //
    private static Color colors[] = {Color.RED, Color.BLUE, Color.YELLOW, Color.PINK, Color.GREEN};

    private VGroupManager() {
        pcs = new PropertyChangeSupport(this);
    }

    public synchronized static VGroupManager getInstance() {
        if (instance == null) {
            instance = new VGroupManager();
        }
        return instance;
    }

    public boolean hasGroup(String name) {
        for (VisualizationGroup vg : groups.values()) {
            if (name.equals(vg.getName())) {
                return true;
            }
        }
        return false;
    }

    public List<VisualizationGroup> getGroups() {
        List<VisualizationGroup> ret = new ArrayList<VisualizationGroup>();
        for (VisualizationGroup g : groups.values()) {
            if (g.isActive()) {
                ret.add(g);
            }
        }
        return ret;
    }

    public List<Pair<VisualizationGroup, Distribution>> getDistributions(String attrName) {
        List<Pair<VisualizationGroup, Distribution>> ret = new ArrayList<>();
        for (VisualizationGroup vg : getGroups()) {
            Distribution dist = vg.getDistribution(attrName);
            if (!dist.getMap().isEmpty()) {
                ret.add(new Pair<>(vg, dist));
            }
        }
        return ret;
    }

    public List<Pair<VisualizationGroup, Tree<Long>>> getHierarchies(String attrName) {
        List<Pair<VisualizationGroup, Tree<Long>>> ret = new ArrayList<>();
        for (VisualizationGroup vg : getGroups()) {
            Tree<Long> tree = vg.getHierarchy(attrName);
            if (!tree.isEmpty()) {
                ret.add(new Pair<>(vg, tree));
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
        VisualizationGroup group = new VisualizationGroup(groupCount, newName, groupColor);
        group.addPropertyChangeListener(this);
        groups.put(groupCount, group);
        groupCount++;
        //firePropertyChange(VISGROUP_NUM_CHANGED, 0, newName);
        return group;
    }

    public void removeGroup(VisualizationGroup vg) {
        groups.remove(vg.getId());
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
        for (VisualizationGroup vg : groups.values()) {
            if (name.equals(vg.getName())) {
                return vg;
            }
        }
        System.err.println("group "+name+" not found");
        assert false; // shouldn't happen
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
