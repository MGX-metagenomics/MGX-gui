package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Children;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class MGXFileNode extends MGXNodeBase<MGXFile> {

    public MGXFileNode(MGXFile f, MGXMaster m) {
        super(Children.LEAF, Lookups.fixed(m, f), f);
        master = m;
        setDisplayName(f.getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/File.png");
        setShortDescription(f.getName());
    }

    @Override
    public boolean canDestroy() {
        return true;
    }
    
    @Override
    public Action[] getActions(boolean context) {
        //return new Action[]{DeleteAction.get(DeleteAction.class)};
        return new Action[]{new DeleteFile()};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getContent().getName());
    }

    public class DeleteFile extends AbstractAction {

        public DeleteFile() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final MGXFile file = getLookup().lookup(MGXFile.class);
            NotifyDescriptor d = new NotifyDescriptor("Really delete file " + file.getName() + "?",
                    "Delete file",
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
                };

                TaskManager.getInstance().addTask("Delete " + file.getName(), deleteTask);
            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
    }
}
