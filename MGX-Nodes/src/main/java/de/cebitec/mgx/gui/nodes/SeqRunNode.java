package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.wizard.seqrun.SeqRunWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class SeqRunNode extends MGXNodeBase {

    public SeqRunNode(MGXMaster m, SeqRun s) {
        this(s);
        master = m;
        setDisplayName(s.getSequencingMethod() + " run");
    }

    private SeqRunNode(SeqRun s) {
        super(Children.LEAF, Lookups.singleton(s));
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/SeqRun.png");
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditSeqRun()};
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
            }
        }
    }
}
