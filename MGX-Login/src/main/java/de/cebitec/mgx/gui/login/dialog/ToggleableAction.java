package de.cebitec.mgx.gui.login.dialog;

import de.cebitec.mgx.gui.login.LoginState;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author sjaenick
 */
@ActionID(category = "File",
        id = "de.cebitec.mgx.gui.login.dialog.ToggleableAction")
@ActionRegistration(displayName = "#CTL_LoginAction",
        iconBase = "de/cebitec/mgx/gui/login/dialog/Login.png")
        //lazy = false)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1100),
    @ActionReference(path = "Toolbars/File", position = 300),
    @ActionReference(path = "Shortcuts", name = "D-L")
})
@Messages("CTL_LoginAction=Login")
public class ToggleableAction extends AbstractAction implements PropertyChangeListener {

    public ToggleableAction() {
        super();
        putValue("iconBase", "de/cebitec/mgx/gui/login/dialog/Login.png");
        LoginState.getInstance().addPropertyChangeListener(this);
    }

    public ToggleableAction(String name) {
        super(name);
        putValue("iconBase", "de/cebitec/mgx/gui/login/dialog/Login.png");
        LoginState.getInstance().addPropertyChangeListener(this);
    }

    public ToggleableAction(String name, Icon icon) {
        super(name, icon);
        putValue("iconBase", "de/cebitec/mgx/gui/login/dialog/Login.png");
        LoginState.getInstance().addPropertyChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LoginHandler.getDefault().showDialog();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("toggleState")) {
            setEnabled(false);
        }
    }
}
