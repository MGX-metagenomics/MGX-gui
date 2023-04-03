/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.wizard.sample.SampleWizardDescriptor;
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
 * @author sj
 */
public class EditSample extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;

    public EditSample() {
        super.putValue(NAME, "Edit");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SampleI sample = Utilities.actionsGlobalContext().lookup(SampleI.class);
        final MGXMasterI m = sample.getMaster();
        SampleWizardDescriptor swd = new SampleWizardDescriptor(m, sample);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(swd);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = swd.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final String oldDisplayName = sample.getMaterial();
            final SampleI s = swd.getSample();
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    m.Sample().update(s);
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
            worker.execute();
        }
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && RBAC.isUser();
    }

}
