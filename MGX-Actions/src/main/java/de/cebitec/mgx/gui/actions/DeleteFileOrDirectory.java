package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.io.Serial;
import java.util.Collection;
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
@ActionID(category = "Edit", id = "de.cebitec.mgx.gui.actions.DeleteFileOrDirectory")
@ActionRegistration(displayName = "Delete", lazy = true)
public class DeleteFileOrDirectory extends NodeAction implements LookupListener {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final Lookup context;
    private Lookup.Result<MGXFileI> lkpInfo;

    public DeleteFileOrDirectory() {
        this(Utilities.actionsGlobalContext());
    }

    private DeleteFileOrDirectory(Lookup context) {
        putValue(NAME, "Delete");
        this.context = context;
        init();
    }

    private void init() {
        if (lkpInfo != null) {
            return;
        }
        lkpInfo = context.lookupResult(MGXFileI.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
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
        return "Delete";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected void performAction(Node[] activatedNodes) {
        Collection<? extends MGXFileI> files = lkpInfo.allInstances();
        if (files.isEmpty()) {
            return;
        }

        for (MGXFileI file : files) {
            NotifyDescriptor d = new NotifyDescriptor("Really delete " + file.getName() + "?",
                    "Delete file/directory",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {

                final DeleteFileTask deleteTask = new DeleteFileTask(file);

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
        return super.isEnabled() && RBAC.isUser() && !lkpInfo.allInstances().isEmpty();
    }


    private final static class DeleteFileTask extends MGXTask {

        private final MGXFileI file;

        public DeleteFileTask(MGXFileI file) {
            super("Delete " + file.getName());
            this.file = file;
        }

        @Override
        public boolean process() {
            setStatus("Deleting..");
            TaskI<MGXFileI> delTask;
            try {
                delTask = file.getMaster().File().delete(file);
            } catch (MGXException ex) {
                setStatus(ex.getMessage());
                return false;
            }
            while (delTask != null && !delTask.done()) {
                sleep();
                try {
                    file.getMaster().<MGXFileI>Task().refresh(delTask);
                } catch (MGXException ex) {
                    setStatus(ex.getMessage());
                    failed(ex.getMessage());
                    return false;
                }
            }
            return true;
        }
    }
}
