/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.util.Collection;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

@ActionID(
        category = "Edit",
        id = "de.cebitec.mgx.gui.actions.RestartJobAction"
)
@ActionRegistration(
        displayName = "#CTL_RestartJobAction",
        lazy = false
)
//@ActionReference(path = "Menu/File", position = 1300)
@Messages("CTL_RestartJobAction=RestartJobAction")
public final class RestartJobAction extends NodeAction {

    @Override
    protected void performAction(Node[] activatedNodes) {

        for (final JobI job : Utilities.actionsGlobalContext().lookupAll(JobI.class)) {

            // skip over jobs that are still running or being deleted
            JobState state = job.getStatus();
            if (!(state.equals(JobState.ABORTED) || state.equals(JobState.FAILED))) {
                continue;
            }

            if (job.getSeqrun() == null) {
                throw new RuntimeException("Internal error: Job has no sequencing run.");
            }
            if (job.getTool() == null) {
                throw new RuntimeException("Internal error: Job has no tool.");
            }

            final MGXTask restartTask = new MGXTask("Restart " + job.getTool().getName()) {
                @Override
                public boolean process() {
                    setStatus("Restarting job..");
                    TaskI<JobI> task = null;
                    try {
                        task = job.getMaster().Job().restart(job);
                    } catch (MGXException ex) {
                        setStatus(ex.getMessage());
                        failed(ex.getMessage());
                        return false;
                    }
                    while (task != null && !task.done()) {
                        setStatus(task.getStatusMessage());
                        try {
                            job.getMaster().<JobI>Task().refresh(task);
                        } catch (MGXException ex) {
                            setStatus(ex.getMessage());
                            failed(ex.getMessage());
                            return false;
                        }
                        sleep();
                    }
                    if (task != null) {
//                        task.finish();
                        job.modified();
                        return task.getState() == TaskI.State.FINISHED;
                    }
                    return false;
                }
            };
            NonEDT.invoke(new Runnable() {
                @Override
                public void run() {
                    TaskManager.getInstance().addTask(restartTask);
                }
            });
        }
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (!(RBAC.isUser() || RBAC.isAdmin())) {
            return false;
        }
        if (activatedNodes.length == 0) {
            return false;
        }

        Collection<? extends JobI> jobs = Utilities.actionsGlobalContext().lookupAll(JobI.class);
        for (JobI j : jobs) {
            JobState state = j.getStatus();
            if (!(state.equals(JobState.FAILED) || state.equals(JobState.ABORTED))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getName() {
        return "Restart";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
