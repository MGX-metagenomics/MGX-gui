package de.cebitec.mgx.gui.rbac;

import de.cebitec.mgx.api.MGXMasterI;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class RBAC {

    public static boolean isAdmin() {
        MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        return m != null && !m.isDeleted() && m.getRoleName().equals("Admin");
    }

    public static boolean isUser() {
        MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        // Admins are treated as users, as well..
        return m != null && !m.isDeleted() && (isAdmin() || m.getRoleName().equals("User"));
    }

    public static boolean isGuest() {
        MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        return m != null && !m.isDeleted() && m.getRoleName().equals("Guest");
    }
}
