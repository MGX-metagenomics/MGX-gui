package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.nodefactory.DNAExtractNodeFactory;
import de.cebitec.mgx.gui.swingutils.NonEDT;
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
public class SampleNode extends MGXNodeBase<SampleI, SampleNode> {

    private DNAExtractNodeFactory nf = null;

    public SampleNode(MGXMasterI m, SampleI s) {
        this(m, s, new DNAExtractNodeFactory(m, s));
        master = m;
    }

    private SampleNode(MGXMasterI m, SampleI s, DNAExtractNodeFactory snf) {
        super(Children.create(snf, true), Lookups.fixed(m, s), s);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Sample.png");
        setShortDescription(getToolTipText(s));
        setDisplayName(s.getMaterial());
        this.nf = snf;
    }

    private String getToolTipText(SampleI s) {
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

    @Override
    public void updateModified() {
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Sample.png");
        setShortDescription(getToolTipText(getContent()));
        setDisplayName(getContent().getMaterial());
    }

    private class EditSample extends AbstractAction {

        public EditSample() {
            putValue(NAME, "Edit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
            SampleI sample = getLookup().lookup(SampleI.class);

            SampleWizardDescriptor swd = new SampleWizardDescriptor(m, sample);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(swd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = swd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                final String oldDisplayName = sample.getMaterial();
                final SampleI s = swd.getSample();
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
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
//                        setShortDescription(getToolTipText(s));
//                        setDisplayName(s.getMaterial());
//                        fireDisplayNameChange(oldDisplayName, s.getMaterial());
                        super.done();
                    }
                };
                worker.execute();
            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
    }

    private class DeleteSample extends AbstractAction {

        public DeleteSample() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final SampleI sample = getLookup().lookup(SampleI.class);
            final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
            NotifyDescriptor d = new NotifyDescriptor("Really delete sample " + sample.getMaterial() + "?",
                    "Delete sample",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                final MGXTask deleteTask = new MGXTask("Delete " + sample.getMaterial()) {
                    @Override
                    public boolean process() {
                        setStatus("Deleting..");
                        TaskI task = m.Sample().delete(sample);
                        while (!task.done()) {
                            setStatus(task.getStatusMessage());
                            task = m.Task().refresh(task);
                            sleep();
                        }
                        task.finish();
                        return task.getState() == TaskI.State.FINISHED;

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

    private class AddExtract extends AbstractAction {

        public AddExtract() {
            putValue(NAME, "Add DNA extract");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
            DNAExtractWizardDescriptor wd = new DNAExtractWizardDescriptor(m);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                SampleI s = getLookup().lookup(SampleI.class);
                final DNAExtractI extract = wd.getDNAExtract();
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

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
    }
}
