package de.cebitec.mgx.gui.login.dialog;

import de.cebitec.gpms.rest.GPMSClientI;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.util.NbPreferences;
import de.cebitec.mgx.gui.login.configuration.MGXserverPanel;
import de.cebitec.mgx.restgpms.GPMS;

/**
 *
 * @author sj
 */
public class LoginHandler implements ActionListener {

    private static LoginHandler instance = new LoginHandler();
    private LoginPanel panel = new LoginPanel();
    private DialogDescriptor dialog = null;
    private String server = null;

    private LoginHandler() {
    }

    public static LoginHandler getDefault() {
        return instance;
    }

    public void showDialog() {
        dialog = new DialogDescriptor(panel, "Login", true, this);
        dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION});
        NotificationLineSupport nline = dialog.createNotificationLineSupport();
        dialog.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(DialogDescriptor.CLOSED_OPTION)) {
                    dialog = null;
                }
            }
        });

        panel.setUser(NbPreferences.forModule(MGXserverPanel.class).get("lastLogin", ""));

        server = NbPreferences.forModule(MGXserverPanel.class).get("server", "");
        if ("".equals(server)) {
            nline.setErrorMessage("No server configured!");
            dialog.setValid(false);
        } else {
            nline.setInformationMessage("Current server is "+server);
        }

        DialogDisplayer.getDefault().notify(dialog);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == DialogDescriptor.OK_OPTION) {
            String user = panel.getUser();
            String password = panel.getPassword();
            NbPreferences.forModule(MGXserverPanel.class).put("lastLogin", user);
            GPMSClientI gpms = new GPMS(server);
            if (!gpms.login(user, password)) {
                dialog.getNotificationLineSupport().setErrorMessage("Login failed.");
            }
        }
    }
}
