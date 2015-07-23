
package de.cebitec.mgx.gui.actions;

import javax.swing.AbstractAction;

/**
 *
 * @author sj
 */
public abstract class OpenMappingBase extends AbstractAction {

    public OpenMappingBase() {
        putValue(NAME, "Show reference mapping");
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

}
