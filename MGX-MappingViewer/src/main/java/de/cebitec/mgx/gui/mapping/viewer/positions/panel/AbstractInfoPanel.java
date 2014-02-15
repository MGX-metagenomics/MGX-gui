package de.cebitec.mgx.gui.mapping.viewer.positions.panel;

import de.cebitec.mgx.gui.mapping.viewer.positions.MousePositionListenerI;
import javax.swing.JPanel;

/**
 *
 * @author ddoppmeier
 */
public abstract class AbstractInfoPanel extends JPanel implements MousePositionListenerI{
    
    private static final long serialVersionUID = 1L;
    public abstract void close();
}
