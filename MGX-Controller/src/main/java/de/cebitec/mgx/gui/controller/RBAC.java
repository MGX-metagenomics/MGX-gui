package de.cebitec.mgx.gui.controller;

import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class RBAC {

    public static boolean isAdmin() {
        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        return m != null && m.getMembership().getRole().getName().equals("Admin");
    }

    public static boolean isUser() {
        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        // Admins are treated as users, as well..
        return m != null && (isAdmin() || m.getMembership().getRole().getName().equals("User"));
    }

    public static boolean isGuest() {
        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        return m != null && m.getMembership().getRole().getName().equals("Guest");
    }
}
