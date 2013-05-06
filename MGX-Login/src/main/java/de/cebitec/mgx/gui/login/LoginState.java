package de.cebitec.mgx.gui.login;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author sjaenick
 */
public class LoginState {

    private boolean loggedIn = false;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private static LoginState instance = new LoginState();

    private LoginState() {
    }

    public static LoginState getInstance() {
        return instance;
    }

    public boolean loggedIn() {
        return loggedIn;
    }

    public void disable() {
        loggedIn = true;
        pcs.firePropertyChange("toggleState", false, true);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
}
