package de.cebitec.mgx.common.visualization;

import de.cebitec.mgx.api.groups.ImageExporterI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Visualizable;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.common.VGroupManager;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @param <T>
 * @author sjaenick
 */
public abstract class ViewerI<T extends Visualizable> implements Comparable<ViewerI<T>> { //, VisFilterI<T>  {

    private AttributeTypeI attrType;
    private String chartTitle;
    private VGroupManagerI mgr;

    public ViewerI() {
    }

    public VGroupManagerI getVGroupManager() {
        return mgr != null ? mgr : VGroupManager.getInstance();
    }

    public void setVGroupManager(VGroupManagerI mgr) {
        this.mgr = mgr;
    }

    /**
     *
     * @return main component representing the visualization
     */
    public abstract JComponent getComponent();

    /**
     *
     * @return exporter instance able to save the visualization
     */
    public abstract ImageExporterI getImageExporter();

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
    public abstract boolean canHandle(AttributeTypeI valueType);

    /**
     *
     * @return the expected class of data to be displayed
     */
    public abstract Class getInputType();

    /**
     *
     * @param dists distributions to be displayed
     */
    public abstract void show(List<Pair<VisualizationGroupI, T>> dists);

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

    /**
     *
     * @param aType indicates the attribute type to be displayed
     */
    public void setAttributeType(AttributeTypeI aType) {
        this.attrType = aType;
    }

    /**
     *
     * @return
     */
    protected AttributeTypeI getAttributeType() {
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
