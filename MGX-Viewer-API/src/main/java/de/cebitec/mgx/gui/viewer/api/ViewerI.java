package de.cebitec.mgx.gui.viewer.api;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Visualizable;
import de.cebitec.mgx.api.model.AttributeTypeI;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author sj
 */
public interface ViewerI<T extends Visualizable> extends Comparable<ViewerI<T>> {

    /**
     * @return display name of the viewer
     */
    public String getName();

    /**
     * @param attrType attribute type selected within UI
     * @return true if this viewer can display the attribute type
     */
    public boolean canHandle(AttributeTypeI attrType);

    /**
     * @param attrType indicates the attribute type to be displayed
     */
    public void setAttributeType(AttributeTypeI attrType);

    /**
     * @return main component representing the visualization
     */
    public JComponent getComponent();

    /**
     * @return the expected class of data to be displayed; either
     * DistributionI.class or TreeI.class
     */
    public Class<?> getInputType();

    /**
     * @param dists distributions to be displayed
     */
    public void show(List<Pair<GroupI, T>> dists);

    /**
     * release internal resources
     */
    public void dispose();

}
