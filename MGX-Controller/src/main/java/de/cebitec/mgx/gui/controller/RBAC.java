package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class RBAC {

    public static boolean isAdmin() {
        MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        return m != null && m.getRoleName().equals("Admin");
    }

    public static boolean isUser() {
        MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        // Admins are treated as users, as well..
        return m != null && (isAdmin() || m.getRoleName().equals("User"));
    }

    public static boolean isGuest() {
        MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        return m != null && m.getRoleName().equals("Guest");
    }
}
