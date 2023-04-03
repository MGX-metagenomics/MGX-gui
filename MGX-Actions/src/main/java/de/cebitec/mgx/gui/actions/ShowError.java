/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.common.JobState;
import de.cebitec.mgx.gui.rbac.RBAC;
import java.io.Serial;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

@ActionID(category = "Edit", id = "de.cebitec.mgx.gui.actions.ShowError")
@ActionRegistration(displayName = "Show error", lazy = false)
@Messages("CTL_ShowError=ShowError")
public final class ShowError extends NodeAction {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void performAction(Node[] activatedNodes) {

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
    protected boolean enable(Node[] activatedNodes) {
        if (!(RBAC.isUser() || RBAC.isAdmin())) {
            return false;
        }
        if (activatedNodes.length != 1) {
            return false;
        }

        Collection<? extends JobI> jobs = Utilities.actionsGlobalContext().lookupAll(JobI.class);
        for (JobI j : jobs) {
            JobState state = j.getStatus();
            if (state.equals(JobState.FAILED)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return "Show error";
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
