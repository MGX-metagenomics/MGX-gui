/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.gui.controller.RBAC;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class GetError extends AbstractAction {

    public GetError() {
        super.putValue(NAME, "Show error");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JobI job = Utilities.actionsGlobalContext().lookup(JobI.class);
        final MGXMasterI m = job.getMaster();
        SwingWorker<String, Void> sw = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return m.Job().getErrorMessage(job);
            }

            @Override
            protected void done() {
                NotifyDescriptor nd;
                try {
                    JTextArea area = new JTextArea(get());
                    nd = new NotifyDescriptor.Message(area);
                    DialogDisplayer.getDefault().notify(nd);
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                super.done();
            }
        };
        sw.execute();
    }

    @Override
    public boolean isEnabled() {
        final JobI job = Utilities.actionsGlobalContext().lookup(JobI.class);
        return super.isEnabled() && RBAC.isUser() && job != null && job.getStatus().equals(JobState.FAILED);
    }
    
}
