
package de.cebitec.mgx.gui.actions;

import java.io.Serial;
import javax.swing.AbstractAction;

/**
 *
 * @author sj
 */
public abstract class OpenMappingBase extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public OpenMappingBase() {
        putValue(NAME, "Show reference mapping");
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

}
