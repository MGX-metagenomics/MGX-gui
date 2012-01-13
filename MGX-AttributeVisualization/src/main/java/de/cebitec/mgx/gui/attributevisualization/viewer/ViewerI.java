package de.cebitec.mgx.gui.attributevisualization.viewer;

import de.cebitec.mgx.gui.attributevisualization.filter.VisFilterI;
import javax.swing.JComponent;

/**
 *
 * @author sjaenick
 */
public abstract class ViewerI implements VisFilterI {

    public abstract JComponent getComponent();
    
    public abstract void setTitle(String title);
    
    public abstract String getName();
}
