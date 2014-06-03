package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.actions.OpenMappingByReference;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
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
public class ReferenceNode extends MGXNodeBase<MGXReferenceI, ReferenceNode> {

    public ReferenceNode(MGXMasterI m, MGXReferenceI ref) {
        this(ref, m);
    }

    private ReferenceNode(MGXReferenceI ref, MGXMasterI m) {
        super(Children.LEAF, Lookups.fixed(m, ref), ref);
        master = m;
        setDisplayName(ref.getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(ref));
    }

    private String getToolTipText(MGXReferenceI ref) {
        return new StringBuilder("<html>").append("<b>Reference: </b>")
                .append(ref.getName())
                .append("<br><hr><br>")
                .append(ref.getLength()).append(" bp<br>")
                .append("</html>").toString();
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new OpenMappingByReference(), new DeleteReference()};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(getContent()));
    }


    private class DeleteReference extends AbstractAction {

        public DeleteReference() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final MGXReferenceI ref = getLookup().lookup(MGXReferenceI.class);
            NotifyDescriptor d = new NotifyDescriptor("Really delete reference " + ref.getName() + "?",
                    "Delete reference",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                final MGXTask deleteTask = new MGXTask("Delete " + ref.getName()) {
                    @Override
                    public boolean process() {
                        setStatus("Deleting..");
                        MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
                        TaskI task = m.Reference().delete(ref);
                        while (!task.done()) {
                            setStatus(task.getStatusMessage());
                            task = m.Task().refresh(task);
                            sleep();
                        }
                        task.finish();
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

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
    }
}
