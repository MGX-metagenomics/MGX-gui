package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.AddSeqRun;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.nodefactory.SeqRunNodeFactory;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.extract.DNAExtractWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
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
public class DNAExtractNode extends MGXNodeBase<DNAExtractI> {

    private final SeqRunNodeFactory snf;

    public DNAExtractNode(DNAExtractI d) {
        this(d, new SeqRunNodeFactory(d));
    }

    private DNAExtractNode(DNAExtractI d, SeqRunNodeFactory snf) {
        super(d.getMaster(), Children.create(snf, true), Lookups.fixed(d.getMaster(), d), d);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/DNAExtract.png");
        setShortDescription(getToolTipText(d));
        setDisplayName(d.getName());
        this.snf = snf;
    }

    private String getToolTipText(DNAExtractI d) {
        return new StringBuilder("<html><b>DNA extract: </b>")
                .append(d.getName())
                .append("<br><hr><br>")
                .append("type: ").append(d.getMethod()).append("<br>")
                .append("protocol: ").append(d.getProtocol() != null ? d.getProtocol() : "")
                .append("</html>").toString();
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditDNAExtract(), new DeleteDNAExtract(), new AddSeqRun(snf)};
    }

    @Override
    public void updateModified() {
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/DNAExtract.png");
        setShortDescription(getToolTipText(getContent()));
        setDisplayName(getContent().getName());
    }

    private class EditDNAExtract extends AbstractAction {

        public EditDNAExtract() {
            putValue(NAME, "Edit");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DNAExtractI extract = getLookup().lookup(DNAExtractI.class);
            final MGXMasterI m = extract.getMaster();
            DNAExtractWizardDescriptor wd = new DNAExtractWizardDescriptor(m, extract);
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                final String oldDisplayName = extract.getMethod();
                final DNAExtractI updatedExtract = wd.getDNAExtract();
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        m.DNAExtract().update(updatedExtract);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
//                        setShortDescription(getToolTipText(updatedExtract));
//                        setDisplayName(updatedExtract.getName());
//                        fireDisplayNameChange(oldDisplayName, updatedExtract.getMethod());
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

    private class DeleteDNAExtract extends AbstractAction {

        public DeleteDNAExtract() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final DNAExtractI dna = getLookup().lookup(DNAExtractI.class);
            final MGXMasterI m = dna.getMaster();

            NotifyDescriptor d = new NotifyDescriptor("Really delete DNA extract " + dna.getMethod() + "?",
                    "Delete DNA extract",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
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
