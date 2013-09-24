
package de.cebitec.mgx.gui.wizard.analysis.misc;

import javax.swing.JPanel;

/**
 *
 * @author sjaenick
 */
public abstract class ValueHolderI<T> extends JPanel {
    
    public abstract T getValue();
    
    public abstract void setValue(T value);
    
}
