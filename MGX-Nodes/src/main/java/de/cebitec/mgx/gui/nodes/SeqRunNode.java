package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.actions.DownloadSeqRun;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.JobState;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import static de.cebitec.mgx.gui.datamodel.misc.ToolType.GLOBAL;
import static de.cebitec.mgx.gui.datamodel.misc.ToolType.PROJECT;
import static de.cebitec.mgx.gui.datamodel.misc.ToolType.USER_PROVIDED;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.analysis.AnalysisWizardIterator;
import de.cebitec.mgx.gui.wizard.seqrun.SeqRunWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
public class SeqRunNode extends MGXNodeBase<SeqRun> { // implements Transferable {

    private final Action[] actions = new Action[]{new ExecuteAnalysis(), new EditSeqRun(), new DeleteSeqRun(), new DownloadSeqRun()};
    //
    //public static final DataFlavor DATA_FLAVOR = new DataFlavor(SeqRunNode.class, "SeqRunNode");

    public SeqRunNode(MGXMaster m, SeqRun s, Children children) {
        super(children, Lookups.fixed(m, s), s);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/SeqRun.png");
        setShortDescription(getToolTipText(s));
        master = m;
        setDisplayName(s.getName());
    }

    private String getToolTipText(SeqRun run) {
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
        return actions;
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getToolTipText(getContent()));
    }

    private final class ExecuteAnalysis extends AbstractAction {

        public ExecuteAnalysis() {
            putValue(NAME, "Analyze");
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            SeqRun seqrun = getLookup().lookup(SeqRun.class);
            final MGXMaster m = getLookup().lookup(MGXMaster.class);
            final Set<Reference> references = new HashSet<>();
            NonEDT.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    Iterator<Reference> refiter = m.Reference().fetchall();
                    while (refiter.hasNext()) {
                        references.add(refiter.next());
                    }
                }
            });

            AnalysisWizardIterator iter = new AnalysisWizardIterator(master, references);
            WizardDescriptor wiz = new WizardDescriptor(iter);
            iter.setWizardDescriptor(wiz);
            //             // {0} will be replaced by WizardDescriptor.Panel.getComponent().getName()
            //             // {1} will be replaced by WizardDescriptor.Iterator.name()
            wiz.setTitleFormat(new MessageFormat("{0}"));
            wiz.setTitle("Tool selection");
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                final Tool tool = (Tool) wiz.getProperty(AnalysisWizardIterator.PROP_TOOL);
                final ToolType tooltype = (ToolType) wiz.getProperty(AnalysisWizardIterator.PROP_TOOLTYPE);
                final List<JobParameter> params = (List<JobParameter>) wiz.getProperty(AnalysisWizardIterator.PROP_PARAMETERS);

                final MGXTask submit = new MGXTask("Submit " + getContent().getName() + " / " + tool.getName()) {
                    @Override
                    public boolean process() {
                        try {

                            Tool selectedTool = null;

                            switch (tooltype) {
                                case GLOBAL:
                                    long projToolId = master.Tool().installTool(tool.getId());
                                    selectedTool = master.Tool().fetch(projToolId);
                                    break;
                                case PROJECT:
                                    selectedTool = tool;
                                    break;
                                case USER_PROVIDED:
                                    master.Tool().create(tool);
                                    selectedTool = tool;
                                    break;
                                default:
                                    assert false;
                            }

                            final Job job = new Job();
                            job.setCreator(master.getLogin());
                            job.setTool(selectedTool);
                            job.setStatus(JobState.CREATED);
                            job.setSeqrun(getContent());
                            job.setParameters(params);

                            setStatus("Creating job..");
                            master.Job().create(job);
                            setStatus("Validating configuration..");
                            master.Job().verify(job);
                            setStatus("Submitting..");
                            return master.Job().execute(job);
                        } catch (MGXServerException ex) {
                            setStatus(ex.getMessage());
                            Exceptions.printStackTrace(ex);
                        }
                        return false;
                    }
                };

                NonEDT.invoke(new Runnable() {
                    @Override
                    public void run() {
                        TaskManager.getInstance().addTask(submit);
                    }
                });



            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
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

                final SeqRun run = wd.getSeqRun();
                SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                        m.SeqRun().update(run);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        super.done();
                    }
                };
                sw.execute();

//                setDisplayName(seqrun.getName());
//                setShortDescription(getToolTipText(seqrun));
//                fireDisplayNameChange(oldDisplayName, seqrun.getName());
            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
    }

    private class DeleteSeqRun extends AbstractAction {

        public DeleteSeqRun() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final SeqRun sr = getLookup().lookup(SeqRun.class);
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
                        setStatus("Deleting..");
                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                        Task task = m.SeqRun().delete(sr);
                        while (!task.done()) {
                            setStatus(task.getStatusMessage());
                            task = m.Task().get(task);
                            sleep();
                        }
                        task.finish();
                        return task.getState() == Task.State.FINISHED;
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
