/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.gui.controller.RBAC;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class CancelJob extends AbstractAction {

    public CancelJob() {
        putValue(NAME, "Cancel");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JobI job = Utilities.actionsGlobalContext().lookup(JobI.class);
        final MGXMasterI m = job.getMaster();
        SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return m.Job().cancel(job);
            }
        };
        sw.execute();
        try {
            sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            String msg = ex.getMessage();
            if (ex.getCause() != null && ex.getCause().getMessage() != null) {
                msg = ex.getCause().getMessage();
            }
            NotifyDescriptor nd = new NotifyDescriptor("Could not cancel job: " + msg, "Job cancellation failed", NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
            DialogDisplayer.getDefault().notifyLater(nd);
        }
    }

    @Override
    public boolean isEnabled() {
        final JobI job = Utilities.actionsGlobalContext().lookup(JobI.class);
        JobState state = job.getStatus();
        return super.isEnabled() && RBAC.isUser() && !(state.equals(JobState.FINISHED) || state.equals(JobState.FAILED) || state.equals(JobState.IN_DELETION) || state.equals(JobState.ABORTED));
    }
    
}
