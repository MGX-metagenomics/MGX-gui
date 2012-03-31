package de.cebitec.mgx.gui.treeview.actions;

import java.awt.Color;
import prefuse.Visualization;
import prefuse.action.assignment.ColorAction;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

/**
 * change node color according to their state (normal, search, focus)
 * 
 * @author rbisdorf
 */
public class NodeColorAction extends ColorAction {

    private Color focus = new Color(198, 229, 229, 255);
    private Color search = new Color(255, 190, 190, 255);
    //private Color parentFocus = new Color(164, 193, 193,255);
    private Color normal = new Color(255, 255, 255, 0);

    public NodeColorAction(String group) {
        super(group, VisualItem.FILLCOLOR);
    }

    @Override
    public int getColor(VisualItem item) {
        if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
            TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
            ts.addTuple(item);
            return ColorLib.color(search);
        } else if (m_vis.isInGroup(item, Visualization.FOCUS_ITEMS)) {
            return ColorLib.color(focus);
        } //else if (item.getDOI() > -1)
        //	return ColorLib.color(parentFocus);
        else {
            return ColorLib.color(normal);
        }
    }

    public Color getFocus() {
        return focus;
    }

    public void setFocus(Color focus) {
        this.focus = focus;
    }

    public Color getSearch() {
        return search;
    }

    public void setSearch(Color search) {
        this.search = search;
    }
    
    public Color getNormal() {
        return normal;
    }

    public void setNormal(Color normal) {
        this.normal = normal;
    }
}
