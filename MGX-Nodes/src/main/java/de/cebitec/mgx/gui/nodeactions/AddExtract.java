/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.wizard.extract.DNAExtractWizardDescriptor;
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
public class AddExtract extends AbstractAction {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    public AddExtract() {
        super.putValue(NAME, "Add DNA extract");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        final DNAExtractWizardDescriptor wd = new DNAExtractWizardDescriptor(m);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final SampleI s = Utilities.actionsGlobalContext().lookup(SampleI.class);
            SwingWorker<DNAExtractI, Void> worker = new SwingWorker<DNAExtractI, Void>() {
                @Override
                protected DNAExtractI doInBackground() throws Exception {
                    return m.DNAExtract().create(s, wd.getExtractName(), wd.getMethod(), wd.getProtocol(), wd.getFivePrimer(), wd.getThreePrimer(), wd.getTargetGene(), wd.getTargetFragment(), wd.getDescription());
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    s.childChanged();
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
