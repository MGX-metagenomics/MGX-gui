/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.common.JobState;
import de.cebitec.mgx.gui.rbac.RBAC;
import java.awt.event.ActionEvent;
import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = 1L;
    
    public CancelJob() {
        super.putValue(NAME, "Cancel");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final JobI job = Utilities.actionsGlobalContext().lookup(JobI.class);

        String jobName;
        if (job.getAssembly() != null) {
            jobName = job.getTool().getName() + " / " + job.getAssembly().getName();
        } else if (job.getSeqruns() != null && job.getSeqruns().length > 0) {
            jobName = job.getTool().getName() + " / " + job.getSeqruns()[0].getName();
        } else {
            // should not occur
            jobName = job.getTool().getName();
        }

        NotifyDescriptor d = new NotifyDescriptor("Really cancel job " + jobName + "?", "Cancel job(s)", NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE, null, null);
        Object ret = DialogDisplayer.getDefault().notify(d);

        if (NotifyDescriptor.YES_OPTION.equals(ret)) {

            SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return job.getMaster().Job().cancel(job);
                }

                @Override
                protected void done() {
                    super.done();
                    try {
                        get();
                    } catch (InterruptedException | ExecutionException ex) {
                        String msg = ex.getMessage();
                        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
                            msg = ex.getCause().getMessage();
                        }
                        NotifyDescriptor nd = new NotifyDescriptor("Could not cancel job: " + msg, "Job cancellation failed", NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
                        DialogDisplayer.getDefault().notifyLater(nd);
                    }
                }

            };
            sw.execute();
        }
    }

    @Override
    public boolean isEnabled() {
        final JobI job = Utilities.actionsGlobalContext().lookup(JobI.class);
        if (job == null) {
            return false;
        }
        JobState state = job.getStatus();
        return super.isEnabled() && RBAC.isUser() && !(state.equals(JobState.FINISHED) || state.equals(JobState.FAILED) || state.equals(JobState.IN_DELETION) || state.equals(JobState.ABORTED));
    }

}
