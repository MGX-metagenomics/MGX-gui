package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.MGXFile;
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
        putValue(NAME, "Delete");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final MGXFile file = Utilities.actionsGlobalContext().lookup(MGXFile.class);
        final MGXMasterI master = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        NotifyDescriptor d = new NotifyDescriptor("Really delete " + file.getName() + "?",
                "Delete file/directory",
                NotifyDescriptor.YES_NO_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE,
                null,
                null);
        Object ret = DialogDisplayer.getDefault().notify(d);
        if (NotifyDescriptor.YES_OPTION.equals(ret)) {

            final DeleteTask deleteTask = new DeleteTask(master, file, "Delete " + file.getName());

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

    private final class DeleteTask extends MGXTask {

        private final MGXMasterI master;
        private final MGXFileI file;

        public DeleteTask(MGXMasterI master, MGXFileI file, String name) {
            super(name);
            this.master = master;
            this.file = file;
        }

        @Override
        public boolean process() {
            setStatus("Deleting..");
            TaskI delTask = master.File().delete(file);
            while (!delTask.done()) {
                delTask = master.Task().refresh(delTask);
                sleep();
            }
            delTask.finish();
            return true;
        }
    }
}
