package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.MGXFile;
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
        NotifyDescriptor d = new NotifyDescriptor("Really delete " + file.getName() + "?",
                "Delete file/directory",
                NotifyDescriptor.YES_NO_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE,
                null,
                null);
        Object ret = DialogDisplayer.getDefault().notify(d);
        if (NotifyDescriptor.YES_OPTION.equals(ret)) {
            MGXTask deleteTask = new MGXTask() {
                @Override
                public void process() {
                    setStatus("Deleting..");
                    MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                    m.File().delete(file);
                }

                @Override
                public boolean isDeterminate() {
                    return false;
                }

                @Override
                public int getProgress() {
                    return -1;
                }
            };

            TaskManager.getInstance().addTask("Delete " + file.getName(), deleteTask);
        }
    }

    @Override
    public boolean isEnabled() {
        return (super.isEnabled() && RBAC.isUser());
    }
}
