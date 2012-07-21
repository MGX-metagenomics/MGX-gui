package de.cebitec.mgx.gui.attributevisualization.viewer;

import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @param <T>
 * @author sjaenick
 */
public abstract class ViewerI<T> implements Comparable<ViewerI<T>> { //, VisFilterI<T>  {

    private AttributeType attrType;
    private String chartTitle;

    /**
     *
     * @return main component representing the visualization
     */
    public abstract JComponent getComponent();

    /**
     *
     * @return display name of the viewer
     */
    public abstract String getName();

    /**
     *
     * @param valueType
     * @return true if this viewer can display the attribute type
     */
    public abstract boolean canHandle(AttributeType valueType);

    /**
     *
     * @return the expected class of data to be displayed
     */
    public abstract Class getInputType();

    /**
     *
     * @param dists
     */
    public abstract void show(List<Pair<VisualizationGroup, T>> dists);

    /**
     *
     * @return customizing component
     */
    public abstract JComponent getCustomizer();

    /**
     *
     */
    public void dispose() {
    }

    ;

//    @Override
//    public List<Pair<VisualizationGroup, T>> filter(List<Pair<VisualizationGroup, T>> dists) {
//        show(dists);
//        return null;
//    }
    
    /**
     * 
     * @param aType indicates the attribute type to be displayed
     */
    public void setAttributeType(AttributeType aType) {
        this.attrType = aType;
    }

    /**
     *
     * @return
     */
    protected AttributeType getAttributeType() {
        return attrType;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        chartTitle = title;
    }

    /**
     *
     * @return
     */
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
