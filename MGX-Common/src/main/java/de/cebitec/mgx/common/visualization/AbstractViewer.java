package de.cebitec.mgx.common.visualization;

import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.misc.Visualizable;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.common.VGroupManager;

/**
 *
 * @param <T>
 * @author sjaenick
 */
public abstract class AbstractViewer<T extends Visualizable> implements ViewerI<T> {

    private AttributeTypeI attrType;
    private String chartTitle;
    private VGroupManagerI mgr;

    public AbstractViewer() {
    }

    public VGroupManagerI getVGroupManager() {
        return mgr != null ? mgr : VGroupManager.getInstance();
    }

    public void setVGroupManager(VGroupManagerI mgr) {
        this.mgr = mgr;
    }

    /**
     *
     * @return display name of the viewer
     */
    @Override
    public abstract String getName();

    
    /**
     *
     */
    @Override
    public void dispose() {
    }

    /**
     *
     * @param aType indicates the attribute type to be displayed
     */
    @Override
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
