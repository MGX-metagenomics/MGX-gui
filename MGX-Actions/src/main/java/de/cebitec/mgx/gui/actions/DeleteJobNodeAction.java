/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.common.JobState;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.io.Serial;
import java.util.Collection;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

@ActionID(
        category = "Edit",
        id = "de.cebitec.mgx.gui.actions.DeleteJobNodeAction"
)
@ActionRegistration(
        displayName = "#CTL_DeleteJobNodeAction",
        lazy = false
)
//@ActionReference(path = "Menu/File", position = 1300)
@Messages("CTL_DeleteJobNodeAction=DeleteJobNodeAction")
public final class DeleteJobNodeAction extends NodeAction {

    @Serial
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void performAction(Node[] activatedNodes) {

        for (final JobI job : Utilities.actionsGlobalContext().lookupAll(JobI.class)) {

            // skip over jobs that are still running or already being deleted
            JobState state = job.getStatus();
            if (state.equals(JobState.RUNNING) || state.equals(JobState.IN_DELETION)) {
                continue;
            }

            String jobName;
            if (job.getAssembly() != null) {
                jobName = job.getTool().getName() + " / " + job.getAssembly().getName();
            } else if (job.getSeqruns() != null && job.getSeqruns().length > 0) {
                jobName = job.getTool().getName() + " / " + job.getSeqruns()[0].getName();
            } else {
                // should not occur
                jobName = job.getTool().getName();
            }
            
            NotifyDescriptor d = new NotifyDescriptor("Really delete job " + jobName + "?", "Delete job(s)", NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE, null, null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                final MGXTask deleteTask = new MGXTask("Delete " + jobName) {
                    @Override
                    public boolean process() {
                        setStatus("Deleting..");
                        MGXMasterI m = job.getMaster();
                        TaskI<JobI> task;
                        try {
                            task = m.Job().delete(job);
                        } catch (MGXException ex) {
                            setStatus(ex.getMessage());
                            failed(ex.getMessage());
                            return false;
                        }
                        while (!task.done()) {
                            setStatus(task.getStatusMessage());
                            try {
                                m.<JobI>Task().refresh(task);
                            } catch (MGXException ex) {
                                setStatus(ex.getMessage());
                                failed(ex.getMessage());
                                return false;
                            }
                            sleep();
                        }
//                        task.finish();
                        return task.getState() == TaskI.State.FINISHED;
                    }
                };
                NonEDT.invoke(new Runnable() {
                    @Override
                    public void run() {
                        TaskManager.getInstance().addTask(deleteTask);
                    }
                });
            }
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        Collection<? extends JobI> jobs = Utilities.actionsGlobalContext().lookupAll(JobI.class);
        for (JobI j : jobs) {
            JobState state = j.getStatus();
            if (!(state.equals(JobState.RUNNING) || state.equals(JobState.IN_DELETION))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "Delete";
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}
