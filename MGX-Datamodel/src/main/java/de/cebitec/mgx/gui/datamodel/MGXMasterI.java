package de.cebitec.mgx.gui.datamodel;

import de.cebitec.gpms.core.MembershipI;

/**
 *
 * @author sjaenick
 */
public interface MGXMasterI {
    
    public MembershipI getMembership();

    public String getProject();

    public String getLogin();
}
