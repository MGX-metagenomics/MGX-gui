/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.AbstractAction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Utilities;

/**
 *
 * @author sj
 */
public class DeleteSample extends AbstractAction {
    
    @Serial
    private static final long serialVersionUID = 1L;
    
    public DeleteSample() {
        super.putValue(NAME, "Delete");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final SampleI sample = Utilities.actionsGlobalContext().lookup(SampleI.class);
        final MGXMasterI m = sample.getMaster();
        NotifyDescriptor d = new NotifyDescriptor("Really delete sample " + sample.getMaterial() + "?", "Delete sample", NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE, null, null);
        Object ret = DialogDisplayer.getDefault().notify(d);
        if (NotifyDescriptor.YES_OPTION.equals(ret)) {
            final MGXTask deleteTask = new MGXTask("Delete " + sample.getMaterial()) {
                @Override
                public boolean process() {
                    try {
                        setStatus("Deleting..");
                        TaskI<SampleI> task = m.Sample().delete(sample);
                        while (task != null && !task.done()) {
                            setStatus(task.getStatusMessage());
                            m.<SampleI>Task().refresh(task);
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

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && RBAC.isUser();
    }
    
}
