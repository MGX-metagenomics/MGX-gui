/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.wizard.seqrun.SeqRunWizardDescriptor;
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
public class EditSeqRun extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public EditSeqRun() {
        putValue(NAME, "Edit");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final SeqRunI seqrun = Utilities.actionsGlobalContext().lookup(SeqRunI.class);
        SeqRunWizardDescriptor wd = new SeqRunWizardDescriptor(seqrun);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final SeqRunI run = wd.getSeqRun(seqrun.getMaster());
            SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
                    m.SeqRun().update(run);
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
            //                setDisplayName(seqrun.getName());
            //                setShortDescription(getToolTipText(seqrun));
            //                fireDisplayNameChange(oldDisplayName, seqrun.getName());
        }
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && RBAC.isUser();
    }
    
}
