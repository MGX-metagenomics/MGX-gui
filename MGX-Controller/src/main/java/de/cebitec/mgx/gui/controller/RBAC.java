package de.cebitec.mgx.gui.controller;

import de.cebitec.gpms.core.RoleI;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class RBAC {

    public static boolean isUser() {
        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        RoleI role = m.getMembership().getRole();
        return role.getName().equals("User");
    }

    public static boolean isGuest() {
        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        RoleI role = m.getMembership().getRole();
        return role.getName().equals("Guest");
    }
}
