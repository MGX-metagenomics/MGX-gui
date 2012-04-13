package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.seqrun.SeqRunWizardDescriptor;
import java.awt.Dialog;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class SeqRunNode extends MGXNodeBase {

    private SeqRun seqrun;
    private Action[] actions = new Action[]{new EditSeqRun(), new DeleteSeqRun()};

    public SeqRunNode(MGXMaster m, SeqRun s) {
        this(s);
        master = m;
        seqrun = s;
        setDisplayName(s.getSequencingMethod() + " run");
    }

    private SeqRunNode(SeqRun s) {
        super(Children.LEAF, Lookups.singleton(s));
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/SeqRun.png");
        setShortDescription(getToolTipText(s));
    }

    @Override
    public Transferable drag() throws IOException {
        return seqrun;
    }

    private String getToolTipText(SeqRun run) {
        return new StringBuilder("<html><b>").append(run.getSequencingTechnology()).append(" sequencing run </b>(")
                .append(run.getNumSequences()).append(" reads)").toString();
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return actions;
    }

    private class EditSeqRun extends AbstractAction {

        public EditSeqRun() {
            putValue(NAME, "Edit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SeqRun seqrun = getLookup().lookup(SeqRun.class);
            SeqRunWizardDescriptor wd = new SeqRunWizardDescriptor(seqrun);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                String oldDisplayName = seqrun.getSequencingMethod() + " run";
                seqrun = wd.getSeqRun();
                getMaster().SeqRun().update(seqrun);
                fireDisplayNameChange(oldDisplayName, seqrun.getSequencingMethod() + " run");
                setDisplayName(seqrun.getSequencingMethod() + " run");
            }
        }
    }

    private class DeleteSeqRun extends AbstractAction {

        public DeleteSeqRun() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final SeqRun sr = getLookup().lookup(SeqRun.class);
            NotifyDescriptor d = new NotifyDescriptor("Really delete sequencing run " + sr.getSequencingMethod() + "?",
                    "Delete sequencing run",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                // update display name to indicate this object is currently being modified
//                String oldDisplayName = sr.getSequencingMethod() + " run";
//                String newName = new StringBuilder("<html><s>")
//                        .append(sr.getSequencingMethod())
//                        .append(" run</s></html>")
//                        .toString();
//                fireDisplayNameChange(oldDisplayName, newName);
                // FIXME disable actions?

                MGXTask deleteTask = new MGXTask() {

                    @Override
                    public void process() {
                        setStatus("Deleting..");
                        getMaster().SeqRun().delete(sr.getId());
                    }

                    @Override
                    public void finished() {
                        super.finished();
                        fireNodeDestroyed();
                    }
                };

                TaskManager.getInstance().addTask("Delete " + sr.getSequencingMethod(), deleteTask);
            }
        }
    }
}
