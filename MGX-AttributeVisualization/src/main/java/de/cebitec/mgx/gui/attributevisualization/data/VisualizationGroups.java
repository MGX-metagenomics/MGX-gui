package de.cebitec.mgx.gui.attributevisualization.data;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sj
 */
public class VisualizationGroups implements PropertyChangeListener {

    public static final String VISGROUP_NUM_CHANGED = "vgNumChanged";
    private static VisualizationGroups instance = null;
    private Map<String, VisualizationGroup> groups = new HashMap<String, VisualizationGroup>();
    private int groupCount = 1;
    private PropertyChangeSupport pcs = null;
    //
    private static Color colors[] = {Color.RED, Color.BLUE, Color.YELLOW, Color.PINK, Color.GREEN};

    private VisualizationGroups() {
        pcs = new PropertyChangeSupport(this);
    }

    public static VisualizationGroups getInstance() {
        if (instance == null) {
            instance = new VisualizationGroups();
        }
        return instance;
    }

    public Collection<VisualizationGroup> getGroups() {
        List<VisualizationGroup> ret = new ArrayList<VisualizationGroup>();
        for (VisualizationGroup g : groups.values()) {
            if (g.isActive()) {
                ret.add(g);
            }
        }
        return ret;
    }

    public VisualizationGroup createGroup() {
        String newName = "Group " + groupCount;
        while (groups.containsKey(newName)) {
            newName = "Group " + ++groupCount;
        }
        Color groupColor = colors[(groupCount - 1) % colors.length];
        VisualizationGroup group = new VisualizationGroup(newName, groupColor);
        group.addPropertyChangeListener(this);
        groups.put(newName, group);
        groupCount++;
        firePropertyChange(VISGROUP_NUM_CHANGED, 0, newName);
        return group;
    }
    
    public void removeGroup(String name) {
        VisualizationGroup removed = groups.remove(name);
        assert removed != null;
        firePropertyChange(VISGROUP_NUM_CHANGED, 0, removed.getName());
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        firePropertyChange(pce.getPropertyName(), pce.getOldValue(), pce.getNewValue());
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
