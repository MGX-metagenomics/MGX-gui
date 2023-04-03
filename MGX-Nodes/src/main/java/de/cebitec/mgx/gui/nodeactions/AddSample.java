/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.HabitatI;
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
public class AddSample extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;

    public AddSample() {
        super.putValue(NAME, "Add sample");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        final SampleWizardDescriptor wd = new SampleWizardDescriptor(m);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final HabitatI hab = Utilities.actionsGlobalContext().lookup(HabitatI.class);
            SwingWorker<SampleI, Void> worker = new SwingWorker<SampleI, Void>() {
                @Override
                protected SampleI doInBackground() throws Exception {
                    return m.Sample().create(hab, wd.getCollectionDate(), wd.getSampleMaterial(), wd.getTemperature(), wd.getVolume(), wd.getVolumeUnit());
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    hab.childChanged();
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
