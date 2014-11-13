package de.cebitec.mgx.gui.login.dialog;

import de.cebitec.mgx.gui.explorer.ProjectExplorerTopComponent;
import de.cebitec.mgx.gui.login.LoginState;
import de.cebitec.mgx.gui.login.configuration.MGXserverPanel;
import de.cebitec.mgx.restgpms.GPMS;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.Authenticator;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbPreferences;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 *
 * @author sj
 */
public class LoginHandler implements ActionListener {

    private final static LoginHandler instance = new LoginHandler();
    //
    private final LoginPanel panel = new LoginPanel();
    private DialogDescriptor dialog = null;
    private NotificationLineSupport nline;
    //
    private String servername = null;
    private String serveruri = null;

    private LoginHandler() {
        Authenticator.setDefault(null);
    }

    public static LoginHandler getDefault() {
        return instance;
    }

    public void showDialog() {
        if (!checkVersion()) {
            return;
        }
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

        servername = NbPreferences.forModule(MGXserverPanel.class).get("servername", "CeBiTec");
        //servername = NbPreferences.forModule(MGXserverPanel.class).get("servername", "scooter-TEST");
        serveruri = NbPreferences.forModule(MGXserverPanel.class).get("serveruri", "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/");
        //serveruri = NbPreferences.forModule(MGXserverPanel.class).get("serveruri", "http://scooter.cebitec.uni-bielefeld.de:8080/MGX-maven-web/webresources/");
        if ("".equals(serveruri)) {
            dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION});
            nline.setErrorMessage("No server configured!");
            dialog.setValid(false);
        } else {
            nline.setInformationMessage("Current server is " + servername);
        }

        DialogDisplayer.getDefault().notify(dialog);
    }

    private boolean checkVersion() {
        String version = System.getProperty("java.version");
        float ver = Float.valueOf(version.substring(0, 3));
        if (ver < 1.7) {
            String msg = "Your Java runtime version ("+version+") is too old. MGX requires at least Java 7.";
            NotifyDescriptor nd = new NotifyDescriptor(msg, "Java too old", 
                    NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
            DialogDisplayer.getDefault().notify(nd);
            return false;
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == DialogDescriptor.OK_OPTION) {
            String user = panel.getUser();
            String password = panel.getPassword();
            NbPreferences.forModule(MGXserverPanel.class).put("lastLogin", user);
            final GPMS gpms = new GPMS(servername, serveruri);
            try {
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                if (gpms.login(user, password)) {
                    dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION, DialogDescriptor.OK_OPTION});
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            LoginState.getInstance().disable();
                            openProjectExplorer(gpms);
                            startPing(gpms);
                        }
                    });
                } else {
                    dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION});
                    nline.setErrorMessage("Login failed: " + gpms.getError());
                }
            } finally {
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    private static void openProjectExplorer(final GPMS gpms) {
        ProjectExplorerTopComponent pe = new ProjectExplorerTopComponent();
        pe.setVisible(true);
        pe.setGPMS(gpms);

        Mode m = WindowManager.getDefault().findMode("explorer");
        if (m != null) {
            m.dockInto(pe);
        } else {
            System.err.println("explorer mode not found");
        }
        pe.open();
        pe.requestActive();
    }

    private static void startPing(final GPMS gpms) {
        Thread t = new Thread(new Runnable() {
            private int refresh = -1;
            private long rtt = 0;

            @Override
            public void run() {
                while (LoginState.getInstance().loggedIn()) {
                    refresh++;
                    if (refresh == 30) {
                        refresh = 0;
                        long now = System.currentTimeMillis();
                        long serverTime = gpms.ping();
                        rtt = System.currentTimeMillis() - now;
                    } 
                    StatusDisplayer.getDefault().setStatusText("Connected to " + gpms.getServerName() + " " + rtt + " ms RTT");
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException ex) {
                    }
                }
                StatusDisplayer.getDefault().setStatusText("");
            }
        });
        t.setName("Ping-" + gpms.getServerName());
        t.setDaemon(true);
        t.start();
    }
}
