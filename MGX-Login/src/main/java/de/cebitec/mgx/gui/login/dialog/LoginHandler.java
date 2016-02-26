package de.cebitec.mgx.gui.login.dialog;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.gui.login.configuration.MGXserverPanel;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;

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

    private LoginHandler() {
    }

    public static LoginHandler getDefault() {
        return instance;
    }

    public void showDialog(GPMSClientI gpmsClient) {
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

        if ("".equals(gpmsClient.getBaseURI())) {
            dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION});
            nline.setErrorMessage("No server configured!");
            dialog.setValid(false);
        } else {
            nline.setInformationMessage("Current server is " + gpmsClient.getServerName());
        }

        DialogDisplayer.getDefault().notify(dialog);
    }

    private static boolean checkVersion() {
        String version = System.getProperty("java.version");
        float ver = Float.valueOf(version.substring(0, 3));
        if (ver < 1.7) {
            String msg = "Your Java runtime version (" + version + ") is too old. MGX requires at least Java 7.";
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
            final GPMSClientI gpms = Utilities.actionsGlobalContext().lookup(GPMSClientI.class);
            try {
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                boolean loggedIn = false;
                String errorMsg = "";
                try {
                    loggedIn = gpms.login(user, password);
                } catch (GPMSException ex) {
                    loggedIn = false;
                    errorMsg = ex.getMessage();
                }
                if (loggedIn) {
                    dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION, DialogDescriptor.OK_OPTION});
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
//                            LoginState.getInstance().disable();
//                            openProjectExplorer(gpms);
                            startPing(gpms);
                        }
                    });
                } else {
                    dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION});
                    nline.setErrorMessage("Login failed: " + errorMsg);
                }
            } finally {
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    private static void startPing(final GPMSClientI gpmsClient) {
        Thread t = new Thread(new Runnable() {
            private int refresh = -1;
            private long rtt = 0;

            @Override
            public void run() {
                while (gpmsClient.loggedIn()) {
                    refresh++;
                    if (refresh == 30) {
                        refresh = 0;
                        long now = System.currentTimeMillis();
                        long serverTime = gpmsClient.ping();
                        rtt = System.currentTimeMillis() - now;
                    }
                    StatusDisplayer.getDefault().setStatusText("Connected to " + gpmsClient.getServerName() + " " + rtt + " ms RTT");
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException ex) {
                    }
                }
                StatusDisplayer.getDefault().setStatusText("");
            }
        });
        t.setName("Ping-" + gpmsClient.getServerName());
        t.setDaemon(true);
        t.start();
    }
}
