/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.DNAExtractI;
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
 * @author sjaenick
 */
public class DeleteDNAExtract extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;

    public DeleteDNAExtract() {
        super.putValue(NAME, "Delete");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final DNAExtractI dna = Utilities.actionsGlobalContext().lookup(DNAExtractI.class);
        final MGXMasterI m = dna.getMaster();
        NotifyDescriptor d = new NotifyDescriptor("Really delete " + dna.getMethod()
                + " extract " + dna.getName() + "?", "Delete DNA extract",
                NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE, null, null);
        Object ret = DialogDisplayer.getDefault().notify(d);
        if (NotifyDescriptor.YES_OPTION.equals(ret)) {
            final MGXTask deleteTask = new MGXTask("Delete " + dna.getName()) {
                @Override
                public boolean process() {
                    try {
                        setStatus("Deleting..");
                        TaskI<DNAExtractI> task = m.DNAExtract().delete(dna);
                        while (task != null && !task.done()) {
                            setStatus(task.getStatusMessage());
                            m.<DNAExtractI>Task().refresh(task);
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
