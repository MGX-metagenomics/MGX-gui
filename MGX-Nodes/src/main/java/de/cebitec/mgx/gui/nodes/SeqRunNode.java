package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.EditSeqRun;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.actions.DownloadSeqRun;
import de.cebitec.mgx.gui.actions.OpenMappingBySeqRun;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Children;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class SeqRunNode extends MGXNodeBase<SeqRunI> {

    //
    //public static final DataFlavor DATA_FLAVOR = new DataFlavor(SeqRunNode.class, "SeqRunNode");
    public SeqRunNode(SeqRunI s, Children children) {
        super(s.getMaster(), children, Lookups.fixed(s.getMaster(), s), s);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/SeqRun.png");
        setShortDescription(getToolTipText(s));
        setDisplayName(s.getName());
    }

    private String getToolTipText(SeqRunI run) {
        return new StringBuilder("<html><b>Sequencing run: </b>").append(run.getName())
                .append("<br><hr><br>")
                .append(run.getSequencingTechnology().getName()).append(" ")
                .append(run.getSequencingMethod().getName())
                .append("<br>")
                .append(run.getNumSequences()).append(" reads")
                .append("</html>").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        Action analyze = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-ExecuteAnalysis.instance", Action.class);
        return new Action[]{analyze, new OpenMappingBySeqRun(), new EditSeqRun(), new DeleteSeqRun(), new DownloadSeqRun()};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getToolTipText(getContent()));
    }



    private class DeleteSeqRun extends AbstractAction {

        public DeleteSeqRun() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final SeqRunI sr = getLookup().lookup(SeqRunI.class);
            NotifyDescriptor d = new NotifyDescriptor("Really delete sequencing run " + sr.getName() + "?",
                    "Delete sequencing run",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {

                final MGXTask deleteTask = new MGXTask("Delete " + sr.getName()) {
                    @Override
                    public boolean process() {
                        try {
                            setStatus("Deleting..");
                            MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
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
                            failed();
                            return false;
                        }
                    }

                    @Override
                    public void finished() {
                        super.finished();
                        fireNodeDestroyed();
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
