/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.common.JobState;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.io.Serial;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
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

    @Serial
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void performAction(Node[] activatedNodes) {

        final Collection<? extends JobI> jobs = Utilities.actionsGlobalContext().lookupAll(JobI.class);
        
        // leave EDT
        NonEDT.invoke(new Runnable() {
            @Override
            public void run() {
                for (final JobI job : jobs) {

                    // skip over jobs that are still running or being deleted
                    if (!(job.getStatus().equals(JobState.ABORTED) || job.getStatus().equals(JobState.FAILED))) {
                        continue;
                    }

                    if (job.getTool() == null) {
                        throw new RuntimeException("Internal error: Job has no tool.");
                    }

                    final CountDownLatch restartComplete = new CountDownLatch(1);

                    NonEDT.invoke(new Runnable() {
                        @Override
                        public void run() {
                            TaskManager.getInstance().addTask(new RestartTask(job, restartComplete));
                        }
                    });

                    //
                    // throttle submission of subsequent jobs to prevent overloading
                    // the server instance
                    //
                    try {
                        restartComplete.await();
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

        });

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
        return !jobs.isEmpty();
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

    private final static class RestartTask extends MGXTask {

        private final JobI job;
        private final CountDownLatch done;

        public RestartTask(JobI job, CountDownLatch done) {
            super("Restart " + job.getTool().getName());
            this.job = job;
            this.done = done;
        }

        @Override
        public boolean process() {
            setStatus("Restarting job..");
            TaskI<JobI> task = null;
            try {
                task = job.getMaster().Job().restart(job);
            } catch (MGXException ex) {
                setStatus(ex.getMessage());
                failed(ex.getMessage());
                done.countDown();
                return false;
            }
            while (task != null && !task.done()) {
                setStatus(task.getStatusMessage());
                try {
                    job.getMaster().<JobI>Task().refresh(task);
                } catch (MGXException ex) {
                    setStatus(ex.getMessage());
                    failed(ex.getMessage());
                    done.countDown();
                    return false;
                }
                sleep();
            }
            if (task != null) {
                if (!job.isDeleted()) {
                    job.modified();
                }
                done.countDown();
                return task.getState() == TaskI.State.FINISHED;
            }
            done.countDown();
            return false;
        }
    };
}
