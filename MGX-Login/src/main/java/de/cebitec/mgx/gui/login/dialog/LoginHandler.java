package de.cebitec.mgx.gui.login.dialog;

import de.cebitec.gpms.core.GPMSException;
import de.cebitec.gpms.core.GPMSMessageI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.gui.login.configuration.MGXserverPanel;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotificationLineSupport;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.modules.Places;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;

/**
 *
 * @author sj
 */
public class LoginHandler implements ActionListener {

    private final static LoginHandler instance = new LoginHandler();
    //
    private final LoginPanel panel;
    private final DialogDescriptor dialog;
    private NotificationLineSupport nline = null;
    //
    private PingMaster pingMaster = null;
    private static final Logger LOG = Logger.getLogger(LoginHandler.class.getName());

    private LoginHandler() {
        panel = new LoginPanel(this);
        dialog = new DialogDescriptor(panel, "Login", true, this);
        nline = dialog.createNotificationLineSupport();
    }

    public static LoginHandler getDefault() {
        return instance;
    }

    public void showDialog(GPMSClientI gpmsClient) {
        //dialog = new DialogDescriptor(panel, "Login", true, this);
        dialog.setValid(false);
        dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION, DialogDescriptor.OK_OPTION});

        PropertyChangeListener pcl = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(DialogDescriptor.PROP_VALUE)
                        && evt.getNewValue().equals(DialogDescriptor.CLOSED_OPTION)) {
                    dialog.setClosingOptions(null);
                }
            }
        };

        dialog.addPropertyChangeListener(pcl);

        // restore saved settings
        panel.setUser(NbPreferences.forModule(MGXserverPanel.class).get("lastLogin" + gpmsClient.getServerName(), ""));
        panel.setPassword("");

        if ("".equals(gpmsClient.getBaseURI())) {
            dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION});
            nline.setErrorMessage("No server configured!");
            dialog.setValid(false);
        } else {
            nline.setInformationMessage("Current server is " + gpmsClient.getServerName());
        }

        DialogDisplayer.getDefault().notify(dialog);

        dialog.removePropertyChangeListener(pcl);
    }

    void update(boolean valid) {
        dialog.setValid(valid);

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == DialogDescriptor.OK_OPTION) {
            String user = panel.getUser();
            String password = panel.getPassword();

            final GPMSClientI gpmsClient = Utilities.actionsGlobalContext().lookup(GPMSClientI.class);

            if (gpmsClient == null) {
                return;
            }
            NbPreferences.forModule(MGXserverPanel.class).put("lastLogin" + gpmsClient.getServerName(), user);

            try {
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                boolean loggedIn = false;
                String errorMsg = "";
                try {
                    loggedIn = gpmsClient.login(user, password);
                } catch (GPMSException ex) {
                    loggedIn = false;
                    errorMsg = ex.getMessage();
                    while (errorMsg.contains("Exception:")) {
                        errorMsg = errorMsg.substring(errorMsg.indexOf("Exception:") + 11);
                    }
                    if (!"Wrong username/password".equals(errorMsg)) {
                        LOG.log(Level.SEVERE, null, ex);
                    }

                }
                if (loggedIn) {
                    dialog.setClosingOptions(new Object[]{DialogDescriptor.CANCEL_OPTION, DialogDescriptor.OK_OPTION});
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            startPing(gpmsClient);
                        }
                    });
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            File cacheDir = new File(Places.getUserDirectory(), "news");
                            if (!cacheDir.exists()) {
                                cacheDir.mkdirs();
                            }

                            Iterator<GPMSMessageI> iter;
                            try {
                                iter = gpmsClient.getMessages();

                                while (iter != null && iter.hasNext()) {
                                    GPMSMessageI msg = iter.next();
                                    File seenMsg = new File(cacheDir, String.valueOf(msg.getDate().getTime()));
                                    if (!seenMsg.exists()) {
                                        NotifyDescriptor nd = new NotifyDescriptor.Message(msg.getText());
                                        DialogDisplayer.getDefault().notify(nd);
                                        seenMsg.createNewFile();
                                    }
                                }
                            } catch (GPMSException | IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
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

    private void startPing(final GPMSClientI gpmsClient) {
        GPMSPinger t = new GPMSPinger(gpmsClient);
        t.start();

        if (pingMaster == null) {
            pingMaster = new PingMaster(t);
            pingMaster.start();
        } else {
            pingMaster.add(t);
        }
    }

    private static class PingMaster extends Thread {

        private final List<GPMSPinger> pingers = new ArrayList<>();

        public PingMaster(GPMSPinger p) {
            pingers.add(p);
            setName("PingMaster");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (true) {
                String statusMessage = "<html>";

                GPMSPinger[] tmp;
                synchronized (pingers) {
                    tmp = pingers.toArray(new GPMSPinger[]{});
                }

                for (GPMSPinger p : tmp) {
                    if (p.isAlive()) {
                        String msg = p.getMessage();
                        if (msg != null) {
                            statusMessage += msg;
                        }
                        if (p != tmp[tmp.length - 1]) {
                            statusMessage += "<b>|</b>";
                        }
                    } else {
                        synchronized (pingers) {
                            pingers.remove(p);
                        }
                    }
                }

                statusMessage += "</html>";

                StatusDisplayer.getDefault().setStatusText(statusMessage);
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException ex) {
                }
            }
            //StatusDisplayer.getDefault().setStatusText("");
        }

        private void add(GPMSPinger t) {
            synchronized (pingers) {
                pingers.add(t);
            }
        }
    }

    private static class GPMSPinger extends Thread {

        private final GPMSClientI gpmsClient;
        private int refresh = 29;
        private long rtt = 0;
        private String text = null;

        public GPMSPinger(GPMSClientI gpmsClient) {
            this.gpmsClient = gpmsClient;
            setName("Ping-" + gpmsClient.getServerName());
            setDaemon(true);
        }

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

                String color = rtt > 10
                        ? rtt > 1000 ? "red" : "orange"
                        : "black";

                text = String.format("&nbsp;%s&nbsp;<font color=\"%s\">%d</font> ms&nbsp;", gpmsClient.getServerName(), color, rtt);
                try {
                    Thread.sleep(2400);
                } catch (InterruptedException ex) {
                }
            }
            text = null;
        }

        public String getMessage() {
            return text;
        }
    }
}
