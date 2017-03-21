/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.gui.rbac.RBAC;
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
public class DeleteHabitat extends AbstractAction {

    public DeleteHabitat() {
        putValue(NAME, "Delete");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final HabitatI habitat = Utilities.actionsGlobalContext().lookup(HabitatI.class);
        final MGXMasterI m = habitat.getMaster();
        NotifyDescriptor d = new NotifyDescriptor("Really delete habitat " + habitat.getName() + "?", "Delete habitat", NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE, null, null);
        Object ret = DialogDisplayer.getDefault().notify(d);
        if (NotifyDescriptor.YES_OPTION.equals(ret)) {
            final MGXTask deleteTask = new MGXTask("Delete " + habitat.getName()) {
                @Override
                public boolean process() {
                    try {
                        setStatus("Deleting..");
                        TaskI<HabitatI> task = m.Habitat().delete(habitat);
                        while (task != null && !task.done()) {
                            setStatus(task.getStatusMessage());
                            m.<HabitatI>Task().refresh(task);
                            sleep();
                        }
//                        if (task != null) {
//                            task.finish();
//                        }
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

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && RBAC.isUser();
    }
    
}
