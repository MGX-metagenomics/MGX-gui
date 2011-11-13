/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.login.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "File",
id = "de.cebitec.mgx.gui.login.dialog.LoginAction")
@ActionRegistration(iconBase = "de/cebitec/mgx/gui/login/dialog/login.png",
displayName = "#CTL_LoginAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1100),
    @ActionReference(path = "Toolbars/File", position = 300),
    @ActionReference(path = "Shortcuts", name = "D-L")
})
@Messages("CTL_LoginAction=Login")
public final class LoginAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        LoginHandler.getDefault().showDialog();
    }
}
