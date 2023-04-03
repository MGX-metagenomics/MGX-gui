/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.wizard.habitat.HabitatWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class EditHabitat extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public EditHabitat() {
        putValue(NAME, "Edit");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HabitatI habitat = Utilities.actionsGlobalContext().lookup(HabitatI.class);
        final MGXMasterI m = habitat.getMaster();
        HabitatWizardDescriptor hwd = new HabitatWizardDescriptor(habitat);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(hwd);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = hwd.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final HabitatI hab = hwd.getHabitat(m);
            SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    m.Habitat().update(hab);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    super.done();
                }
            };
            sw.execute();
        }
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && RBAC.isUser();
    }
    
}
