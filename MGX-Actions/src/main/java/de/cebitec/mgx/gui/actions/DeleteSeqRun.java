/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.io.Serial;
import java.util.Collection;
import javax.swing.Action;
import static javax.swing.Action.NAME;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author sjaenick
 */
@ActionID(category = "Edit", id = "de.cebitec.mgx.gui.actions.DeleteSeqRun")
@ActionRegistration(displayName = "Delete", lazy = false)
public class DeleteSeqRun extends NodeAction implements LookupListener {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Lookup context;
    private Lookup.Result<SeqRunI> lkpInfo;

    public DeleteSeqRun() {
        this(Utilities.actionsGlobalContext());
    }

    private DeleteSeqRun(Lookup context) {
        putValue(NAME, "Delete");
        this.context = context;
        init();
    }

    private void init() {
        if (lkpInfo != null) {
            return;
        }
        lkpInfo = context.lookupResult(SeqRunI.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        return this;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(RBAC.isUser() && !lkpInfo.allInstances().isEmpty());
    }

    @Override
    protected boolean asynchronous() {
        return true;
    }

    @Override
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }

    @Override
    public String getName() {
        return "Analyze";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        Collection<? extends SeqRunI> seqruns = lkpInfo.allInstances();

        for (final SeqRunI sr : seqruns) {
            NotifyDescriptor d = new NotifyDescriptor("Really delete sequencing run " + sr.getName() + "?", "Delete sequencing run", NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE, null, null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                final MGXTask deleteTask = new MGXTask("Delete " + sr.getName()) {
                    @Override
                    public boolean process() {
                        try {
                            setStatus("Deleting..");
                            MGXMasterI m = sr.getMaster();
                            TaskI<SeqRunI> task = m.SeqRun().delete(sr);
                            while (task != null && !task.done()) {
                                setStatus(task.getStatusMessage());
                                m.<SeqRunI>Task().refresh(task);
                                sleep();
                            }
                            return task != null && task.getState() == TaskI.State.FINISHED;
                        } catch (MGXException ex) {
                            setStatus(ex.getMessage());
                            failed(ex.getMessage());
                            return false;
                        }
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
    public boolean isEnabled() {
        init();
        return super.isEnabled() && RBAC.isUser();
    }
}
