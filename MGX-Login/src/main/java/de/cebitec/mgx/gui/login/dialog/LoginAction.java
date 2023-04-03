/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.login.dialog;

import de.cebitec.gpms.rest.GPMSClientI;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 *
 * @author sj
 */
@ActionID(category = "File",
        id = "de.cebitec.mgx.gui.login.dialog.LoginAction")
@ActionRegistration(displayName = "not-used", lazy = false)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1100),
    @ActionReference(path = "Toolbars/File", position = 300),
    @ActionReference(path = "Shortcuts", name = "D-L")
})
public class LoginAction extends AbstractAction implements ContextAwareAction, LookupListener, PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final Lookup.Result<GPMSClientI> result;
    private GPMSClientI curClient = null;

    public LoginAction() {
        this(Utilities.actionsGlobalContext());
    }

    private LoginAction(Lookup lkp) {
        super("Login");
        result = lkp.lookupResult(GPMSClientI.class);
        result.addLookupListener(WeakListeners.create(LookupListener.class, this, result));
        super.putValue("iconBase", "de/cebitec/mgx/gui/login/dialog/Login.svg");
        super.setEnabled(false);
    }

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        return new LoginAction(lkp);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        LoginHandler.getDefault().showDialog(curClient);
    }

    @Override
    public synchronized void resultChanged(LookupEvent le) {
        Collection<? extends GPMSClientI> allClients = result.allInstances();
        if (allClients.size() == 1) {
            GPMSClientI newClient = allClients.toArray(new GPMSClientI[]{})[0];

            if (newClient != null && !newClient.equals(curClient)) {
                if (curClient != null) {
                    curClient.removePropertyChangeListener(this);
                }
                newClient.addPropertyChangeListener(this);
                curClient = newClient;
            }
            super.setEnabled(!curClient.loggedIn());
        } else {
            super.setEnabled(false);
        }
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (curClient != null && curClient.equals(evt.getSource())) {
            super.setEnabled(!curClient.loggedIn());
        }
    }
}
