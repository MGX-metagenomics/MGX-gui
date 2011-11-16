package de.cebitec.mgx.gui.login.dialog;

import de.cebitec.mgx.gui.explorer.ProjectExplorerTopComponent;
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
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 *
 * @author sj
 */
public class LoginHandler implements ActionListener {

    private static LoginHandler instance = new LoginHandler();
    private LoginPanel panel = new LoginPanel();
    private DialogDescriptor dialog = null;
    private NotificationLineSupport nline;
    //
    private String servername = null;
    private String serveruri = null;

    private LoginHandler() {
    }

    public static LoginHandler getDefault() {
        return instance;
    }

    public void showDialog() {

        dialog = new DialogDescriptor(panel, "Login", true, this);
        dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION, DialogDescriptor.OK_OPTION});
        nline = dialog.createNotificationLineSupport();
        dialog.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(DialogDescriptor.PROP_VALUE)
                        && evt.getNewValue().equals(DialogDescriptor.CLOSED_OPTION)) {
                    dialog.setClosingOptions(null);
                }
            }
        });

        panel.setUser(NbPreferences.forModule(MGXserverPanel.class).get("lastLogin", ""));
        panel.setPassword("");


        servername = NbPreferences.forModule(MGXserverPanel.class).get("servername", "scooter");
        serveruri = NbPreferences.forModule(MGXserverPanel.class).get("serveruri", "http://scooter.cebitec.uni-bielefeld.de:8080/MGX-maven-web/webresources/");
        if ("".equals(serveruri)) {
            nline.setErrorMessage("No server configured!");
            dialog.setValid(false);
        } else {
            nline.setInformationMessage("Current server is " + servername);
        }

        DialogDisplayer.getDefault().notify(dialog);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == DialogDescriptor.OK_OPTION) {
            nline.clearMessages();
            nline.setInformationMessage("Current server is " + serveruri);
            String user = panel.getUser();
            String password = panel.getPassword();
            NbPreferences.forModule(MGXserverPanel.class).put("lastLogin", user);
            GPMS gpms = new GPMS(servername, serveruri);
            if (gpms.login(user, password)) {
                openProjectExplorer(gpms);
                // TODO: disable 'login' menu entry and shortcut
            } else {
                nline.setErrorMessage("Login failed.");
            }
        }
    }

    private void openProjectExplorer(GPMS gpms) {
        ProjectExplorerTopComponent pe = ProjectExplorerTopComponent.getInstance();
        pe.setGPMSInstance(gpms);
        pe.setVisible(true);
        Mode m = WindowManager.getDefault().findMode("explorer");
        if (m != null) {
            m.dockInto(pe);
        } else {
            System.err.println("explorer mode not found");
        }
        pe.open();
        pe.requestActive();
    }
}
