
package de.cebitec.mgx.gui.wizard.analysis.misc;

import java.io.Serial;
import javax.swing.JPanel;

/**
 *
 * @author sjaenick
 */
public abstract class ValueHolderI extends JPanel {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    public abstract String getValue();
    
    public abstract void setValue(String value);
    
}
