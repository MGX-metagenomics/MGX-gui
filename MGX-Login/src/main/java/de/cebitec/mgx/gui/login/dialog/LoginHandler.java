package de.cebitec.mgx.gui.login.dialog;

import de.cebitec.mgx.gui.explorer.ProjectExplorerTopComponent;
import de.cebitec.mgx.gui.login.configuration.MGXserverPanel;
import de.cebitec.mgx.restgpms.GPMS;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 *
 * @author sj
 */
public class LoginHandler implements ActionListener {

    private static LoginHandler instance = new LoginHandler();
    //
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

        servername = NbPreferences.forModule(MGXserverPanel.class).get("servername", "CeBiTec");
        serveruri = NbPreferences.forModule(MGXserverPanel.class).get("serveruri", "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/");
        //serveruri = NbPreferences.forModule(MGXserverPanel.class).get("serveruri", "http://localhost:8080/MGX-maven-web/webresources/");
        if ("".equals(serveruri)) {
            dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION});
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

    private void openProjectExplorer(final GPMS gpms) {
        ProjectExplorerTopComponent pe = Lookup.getDefault().lookup(ProjectExplorerTopComponent.class);
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

    private void startPing(final GPMS gpms) {
        Thread t = new Thread(new Runnable() {

            private int refresh = -1;
            private long rtt = 0;

            @Override
            public void run() {
                while (true) {

                    refresh++;
                    if (refresh == 30) {
                        refresh = 0;
                        long now = System.currentTimeMillis();
                        long serverTime = gpms.ping();
                        rtt = System.currentTimeMillis();

                        if (serverTime < now || serverTime > rtt) {
                            System.err.println("Client/server clocks out of sync.");
                            return;
                        }

                        rtt = rtt - now;
                    }
                    StatusDisplayer.getDefault().setStatusText("Connected to " + gpms.getServerName() + " " + rtt + " ms RTT");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        });
        t.setName("Ping-" + gpms.getServerName());
        t.setDaemon(true);
        t.start();
    }
}
