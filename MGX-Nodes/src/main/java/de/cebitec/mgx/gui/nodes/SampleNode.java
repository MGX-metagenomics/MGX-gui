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
import java.text.DateFormat;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class SampleNode extends MGXNodeBase<Sample> {

    private DNAExtractNodeFactory nf = null;

    public SampleNode(MGXMaster m, Sample s) {
        this(m, s, new DNAExtractNodeFactory(m, s));
        master = m;

    }

    private SampleNode(MGXMaster m, Sample s, DNAExtractNodeFactory snf) {
        super(Children.create(snf, true), Lookups.fixed(m, s), s);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Sample.png");
        setShortDescription(getToolTipText(s));
        setDisplayName(s.getMaterial());
        this.nf = snf;
    }

    private String getToolTipText(Sample s) {
        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(s.getCollectionDate());
        return new StringBuilder("<html><b>Sample: </b>").append(s.getMaterial())
                .append("<br><hr><br>")
                .append("Collection date: ").append(date).append("<br>")
                .append("</html>").toString();
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
                final String oldDisplayName = sample.getMaterial();
                final Sample s = swd.getSample();
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                        m.Sample().update(s);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        setShortDescription(getToolTipText(s));
                        setDisplayName(s.getMaterial());
                        fireDisplayNameChange(oldDisplayName, s.getMaterial());
                        super.done();
                    }
                };
                worker.execute();
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
            final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                MGXTask deleteTask = new MGXTask() {
                    @Override
                    public void process() {
                        setStatus("Deleting..");
                        m.Sample().delete(sample);
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
                final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                Sample s = getLookup().lookup(Sample.class);
                final DNAExtract extract = wd.getDNAExtract();
                extract.setSampleId(s.getId());
                SwingWorker<Long, Void> worker = new SwingWorker<Long, Void>() {
                    @Override
                    protected Long doInBackground() throws Exception {
                        return m.DNAExtract().create(extract);
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        nf.refreshChildren();
                        super.done();
                    }
                };
                worker.execute();
            }
        }
    }
}
