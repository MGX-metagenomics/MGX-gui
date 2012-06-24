package de.cebitec.mgx.gui.attributevisualization.viewer;

import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author sjaenick
 */
public abstract class ViewerI<T> implements Comparable<ViewerI<T>> { //, VisFilterI<T>  {

    private AttributeType attrType;
    private String chartTitle;

    public abstract JComponent getComponent();

    public abstract String getName();

    public abstract boolean canHandle(AttributeType valueType);
    
    public abstract Class getInputType();
    
    public abstract void show(List<Pair<VisualizationGroup, T>> dists);
    
    public abstract JComponent getCustomizer();
    
    public void dispose() {};

//    @Override
//    public List<Pair<VisualizationGroup, T>> filter(List<Pair<VisualizationGroup, T>> dists) {
//        show(dists);
//        return null;
//    }

//    public void sortAscending(boolean ascending) {
//        this.ascending = ascending;
//    }
//
//    protected boolean sortAscending() {
//        return ascending;
//    }
    
    public void setAttributeType(AttributeType aType) {
        this.attrType = aType;
    }
    
    protected AttributeType getAttributeType() {
        return attrType;
    }

    public void setTitle(String title) {
        chartTitle = title;
    }

    protected String getTitle() {
        return chartTitle;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(ViewerI<T> t) {
        return getName().compareTo(t.getName());
    }
}
