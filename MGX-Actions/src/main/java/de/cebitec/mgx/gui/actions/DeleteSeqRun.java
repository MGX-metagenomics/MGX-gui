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
public class DeleteSeqRun extends AbstractAction {

    public DeleteSeqRun() {
        putValue(NAME, "Delete");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final SeqRunI sr = Utilities.actionsGlobalContext().lookup(SeqRunI.class);
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
                        if (task != null) {
                            task.finish();
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

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && RBAC.isUser();
    }
    
}
