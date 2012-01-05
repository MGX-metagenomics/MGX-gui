package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.nodefactory.DNAExtractNodeFactory;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.extract.DNAExtractWizardDescriptor;
import de.cebitec.mgx.gui.wizard.sample.SampleWizardDescriptor;
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
public class SampleNode extends MGXNodeBase {

    private DNAExtractNodeFactory nf = null;

    public SampleNode(MGXMaster m, Sample s) {
        this(s, new DNAExtractNodeFactory(m, s));
        master = m;
        setDisplayName(s.getMaterial());
    }

    private SampleNode(Sample s, DNAExtractNodeFactory snf) {
        super(Children.create(snf, true), Lookups.singleton(s));
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Sample.png");
        this.nf = snf;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditSample(), new DeleteSample(), new AddExtract()};
    }

    private class EditSample extends AbstractAction {

        public EditSample() {
            putValue(NAME, "Edit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Sample sample = getLookup().lookup(Sample.class);
            SampleWizardDescriptor swd = new SampleWizardDescriptor(sample);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(swd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = swd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                String oldDisplayName = sample.getMaterial();
                sample = swd.getSample();
                getMaster().Sample().update(sample);
                fireDisplayNameChange(oldDisplayName, sample.getMaterial());
            }
        }
    }

    private class DeleteSample extends AbstractAction {

        public DeleteSample() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Sample sample = getLookup().lookup(Sample.class);
            NotifyDescriptor d = new NotifyDescriptor("Really delete sample " + sample.getMaterial() + "?",
                    "Delete sample",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                MGXTask deleteTask = new MGXTask() {

                    @Override
                    public void process() {
                        setStatus("Deleting..");
                        getMaster().Sample().delete(sample.getId());
                    }

                    @Override
                    public void finished() {
                        super.finished();
                        fireNodeDestroyed();
                    }
                };

                TaskManager.getInstance().addTask("Delete " + sample.getMaterial(), deleteTask);
            }
        }
    }

    private class AddExtract extends AbstractAction {

        public AddExtract() {
            putValue(NAME, "Add DNA extract");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DNAExtractWizardDescriptor wd = new DNAExtractWizardDescriptor();
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                Sample s = getLookup().lookup(Sample.class);
                DNAExtract extract = wd.getDNAExtract();
                extract.setSampleId(s.getId());
                getMaster().DNAExtract().create(extract);
                nf.refreshChildren();
            }
        }
    }
}
