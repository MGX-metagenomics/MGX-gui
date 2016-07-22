package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class DeleteFileOrDirectory extends AbstractAction {

    public DeleteFileOrDirectory() {
        super.putValue(NAME, "Delete");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final MGXFileI file = Utilities.actionsGlobalContext().lookup(MGXFileI.class);
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

    @Override
    public boolean isEnabled() {
        return (super.isEnabled() && RBAC.isUser());
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
