
package de.cebitec.mgx.gui.wizard.analysis.misc;

import javax.swing.JPanel;

/**
 *
 * @author sjaenick
 */
public abstract class ValueHolderI extends JPanel {
    
    public abstract String getValue();
    
    public abstract void setValue(String value);
    
}
