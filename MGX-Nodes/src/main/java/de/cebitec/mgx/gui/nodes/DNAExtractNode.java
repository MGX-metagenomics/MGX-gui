package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.nodefactory.SeqRunNodeFactory;
import de.cebitec.mgx.gui.wizard.extract.DNAExtractWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
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
public class DNAExtractNode extends MGXNodeBase {

    private SeqRunNodeFactory snf = null;

    public DNAExtractNode(MGXMaster m, DNAExtract d) {
        this(d, new SeqRunNodeFactory(m, d));
        master = m;
        setDisplayName(d.getMethod());
    }

    private DNAExtractNode(DNAExtract d, SeqRunNodeFactory snf) {
        super(Children.create(snf, true), Lookups.singleton(d));
        this.snf = snf;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditDNAExtract(), new DeleteDNAExtract()};
    }
    private class EditDNAExtract extends AbstractAction {

        public EditDNAExtract() {
            putValue(NAME, "Edit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DNAExtract extract = getLookup().lookup(DNAExtract.class);
            DNAExtractWizardDescriptor wd = new DNAExtractWizardDescriptor(extract);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                String oldDisplayName = extract.getMethod();
                extract = wd.getDNAExtract();
                getMaster().DNAExtract().update(extract);
                fireDisplayNameChange(oldDisplayName, extract.getMethod());
            }
        }
    }
    
    private class DeleteDNAExtract extends AbstractAction {

        public DeleteDNAExtract() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DNAExtract dna = getLookup().lookup(DNAExtract.class);
            NotifyDescriptor d = new NotifyDescriptor("Really delete DNA extract " + dna.getMethod() + "?",
                    "Delete DNA extract",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                getMaster().DNAExtract().delete(dna.getId());
                fireNodeDestroyed();
            }
        }
    }
}
