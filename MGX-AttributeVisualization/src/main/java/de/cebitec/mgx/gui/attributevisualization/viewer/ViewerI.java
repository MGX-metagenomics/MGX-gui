package de.cebitec.mgx.gui.attributevisualization.viewer;

import de.cebitec.mgx.gui.attributevisualization.filter.VisFilter;
import javax.swing.JComponent;

/**
 *
 * @author sjaenick
 */
public abstract class ViewerI implements VisFilter {

    public abstract JComponent getComponent();
    
    public abstract void setTitle(String title);
    
    public abstract String getName();
}
