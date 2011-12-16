package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.nodefactory.SeqRunNodeFactory;
import de.cebitec.mgx.gui.wizard.extract.DNAExtractWizardDescriptor;
import de.cebitec.mgx.gui.wizard.seqrun.SeqRunWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
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
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/DNAExtract.png");
        this.snf = snf;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditDNAExtract(), new DeleteDNAExtract(), new AddSeqRun()};
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

    private class AddSeqRun extends AbstractAction {

        public AddSeqRun() {
            putValue(NAME, "Add sequencing run");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SeqRunWizardDescriptor wd = new SeqRunWizardDescriptor();
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                DNAExtract extract = getLookup().lookup(DNAExtract.class);
                SeqRun seqrun = wd.getSeqRun();
                seqrun.setExtract(extract);
                extract.addSeqRun(seqrun);
                getMaster().SeqRun().create(seqrun);
                snf.refreshChildren();

                // now, we need to create a sequence upload task
                String canonicalPath;
                try {
                    canonicalPath = wd.getSequenceFile().getCanonicalPath();
                    System.err.println("UPLOAD: " + canonicalPath);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
