/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.DNAExtractI;
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
 * @author sjaenick
 */
public class EditDNAExtract extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;
    
    public EditDNAExtract() {
        super.putValue(NAME, "Edit");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DNAExtractI extract = Utilities.actionsGlobalContext().lookup(DNAExtractI.class);
        final MGXMasterI m = extract.getMaster();
        DNAExtractWizardDescriptor wd = new DNAExtractWizardDescriptor(m, extract);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            final String oldDisplayName = extract.getMethod();
            final DNAExtractI updatedExtract = wd.getDNAExtract();
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    m.DNAExtract().update(updatedExtract);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    //                        setShortDescription(getToolTipText(updatedExtract));
                    //                        setDisplayName(updatedExtract.getName());
                    //                        fireDisplayNameChange(oldDisplayName, updatedExtract.getMethod());
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
