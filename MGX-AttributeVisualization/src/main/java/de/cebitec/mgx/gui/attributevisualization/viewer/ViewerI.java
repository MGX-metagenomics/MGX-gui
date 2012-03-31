package de.cebitec.mgx.gui.attributevisualization.viewer;

import de.cebitec.mgx.gui.attributevisualization.filter.VisFilterI;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import javax.swing.JComponent;

/**
 *
 * @author sjaenick
 */
public abstract class ViewerI<T> implements VisFilterI<T>, Comparable<ViewerI<T>> {

    private boolean ascending = true;
    private String chartTitle;

    public abstract JComponent getComponent();

    public abstract String getName();

    public abstract boolean canHandle(AttributeType valueType);
    
    public abstract Class getInputType();

    public void sortAscending(boolean ascending) {
        this.ascending = ascending;
    }

    protected boolean sortAscending() {
        return ascending;
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
