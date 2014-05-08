
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.gui.controller.RBAC;
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
        return super.isEnabled() && RBAC.isUser();
    }

}
