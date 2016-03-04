/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.gui.controller.RBAC;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class DisconnectAction extends AbstractAction {

    public DisconnectAction() {
        putValue(NAME, "Disconnect");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final GPMSClientI gpmsClient = Utilities.actionsGlobalContext().lookup(GPMSClientI.class);
        if (gpmsClient.loggedIn()) {
            gpmsClient.logout();
        }
    }

    @Override
    public boolean isEnabled() {
        final GPMSClientI gpmsClient = Utilities.actionsGlobalContext().lookup(GPMSClientI.class);
        return super.isEnabled() && gpmsClient.loggedIn();
    }

}
