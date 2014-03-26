package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.JobState;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.datamodel.misc.Task.State;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class JobNode extends MGXNodeBase<Job, JobNode> {

    public static String TOOL_PROPERTY = "tool";
    public static String SEQRUN_PROPERTY = "seqrun";
    public static String STATE_PROPERTY = "state";
    //private final Job job;

    public JobNode(MGXMaster m, Job job, Children c) {
        super(Children.LEAF, Lookups.fixed(m, job), job);
        Tool tool = job.getTool();
        setDisplayName(tool.getName());
        String shortDesc = new StringBuilder("<html><b>")
                .append(tool.getName()).append("</b>")
                .append("<br><hr><br>")
                .append("Tool version: ")
                .append(tool.getVersion())
                .append("<br>")
                .append(tool.getDescription())
                .append("<br><br>")
                .append("Job created by: ")
                .append(job.getCreator())
                .append("<br>")
                .append(getParameterToolTip(job))
                .append("</html>")
                .toString();
        setShortDescription(shortDesc);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/AnalysisTasks.png");
    }
    
    private String getParameterToolTip(Job job) {
        if (job.getParameters() == null || job.getParameters().isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (JobParameter jp : job.getParameters()) {
            sb.append(jp.getParameterName())
                    .append(": ")
                    .append(jp.getParameterValue())
                    .append("<br>");
        }
        return sb.toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<>();
        actions.add(new DeleteJob());
        actions.add(new GetError());
        actions.add(new ResubmitAction());
        actions.add(new CancelJob());

        return actions.toArray(new Action[]{});
        //return new Action[]{new DeleteJob(), new GetError(), new ResubmitAction()};
    }

    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();

        Property toolProperty = new PropertySupport.ReadOnly<String>(TOOL_PROPERTY, String.class, "Tool", "Tool name") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return getContent().getTool().getName();
            }
        };
        toolProperty.setValue("suppressCustomEditor", Boolean.TRUE);
        set.put(toolProperty);

        Property runProperty = new PropertySupport.ReadWrite<String>(SEQRUN_PROPERTY, String.class, "Run", "Run name") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return getContent().getSeqrun().getName();
            }

            @Override
            public void setValue(String t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            }
        };
        runProperty.setValue("suppressCustomEditor", Boolean.TRUE);
        set.put(runProperty);

        Property stateProperty = new PropertySupport.ReadWrite<String>(STATE_PROPERTY, String.class, "State", "Job state") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                JobState state = getContent().getStatus();
                String icon = null;
                switch (state) {
                    case FINISHED:
                        icon = "de/cebitec/mgx/gui/nodes/JobStateOK.png";
                        break;
                    case FAILED:
                        icon = "de/cebitec/mgx/gui/nodes/JobStateFAILED.png";
                        break;
                    case RUNNING:
                        icon = "de/cebitec/mgx/gui/nodes/JobStatusRunning.png";
                        break;
                }
                if (icon != null) {
                    setValue("valueIcon", ImageUtilities.loadImage(icon, true));
                }
                return state.toString();
            }

            @Override
            public void setValue(String t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            }
        };
        stateProperty.setValue("suppressCustomEditor", Boolean.TRUE);
        set.put(stateProperty);

        sheet.put(set);

        return sheet;
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getTool().getName());
    }

    private class DeleteJob extends AbstractAction {

        public DeleteJob() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Job job = getLookup().lookup(Job.class);
            NotifyDescriptor d = new NotifyDescriptor("Really delete job?",
                    "Delete job",
                    NotifyDescriptor.YES_NO_OPTION,
                    NotifyDescriptor.QUESTION_MESSAGE,
                    null,
                    null);
            Object ret = DialogDisplayer.getDefault().notify(d);
            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
                final MGXTask deleteTask = new MGXTask("Delete " + job.getTool().getName() + " " + job.getSeqrun().getName()) {
                    @Override
                    public boolean process() {
                        setStatus("Deleting..");
                        MGXMaster m = getLookup().lookup(MGXMaster.class);
                        Task task = m.Job().delete(job);
                        while (!task.done()) {
                            setStatus(task.getStatusMessage());
                            task = m.Task().refresh(task);
                            sleep();
                        }
                        task.finish();
                        return task.getState() == State.FINISHED;
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
            return super.isEnabled() && RBAC.isUser() && !getContent().getStatus().equals(JobState.RUNNING);
        }
    }

    private class GetError extends AbstractAction {

        public GetError() {
            putValue(NAME, "Show error");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Job job = getLookup().lookup(Job.class);
            final MGXMaster m = getLookup().lookup(MGXMaster.class);

            SwingWorker<String, Void> sw = new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return m.Job().getErrorMessage(job);
                }

                @Override
                protected void done() {
                    NotifyDescriptor nd;
                    try {
                        JTextArea area = new JTextArea(get());
                        nd = new NotifyDescriptor.Message(area);
                        DialogDisplayer.getDefault().notify(nd);
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    super.done();
                }
            };
            sw.execute();
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && RBAC.isUser() && getContent().getStatus().equals(JobState.FAILED);
        }
    }

    private class ResubmitAction extends AbstractAction {

        public ResubmitAction() {
            putValue(NAME, "Restart");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Job job = getLookup().lookup(Job.class);
            final MGXMaster m = getLookup().lookup(MGXMaster.class);

            final MGXTask restartTask = new MGXTask("Restart " + job.getTool().getName()) {
                @Override
                public boolean process() {
                    setStatus("Restarting job..");
                    Task task = m.Job().restart(job);
                    while (!task.done()) {
                        setStatus(task.getStatusMessage());
                        task = m.Task().refresh(task);
                        sleep();
                    }
                    task.finish();
                    return task.getState() == Task.State.FINISHED;
                }
            };

            NonEDT.invoke(new Runnable() {
                @Override
                public void run() {
                    TaskManager.getInstance().addTask(restartTask);
                }
            });
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && RBAC.isUser() && getContent().getStatus().equals(JobState.FAILED);
        }
    }

    private class CancelJob extends AbstractAction {

        public CancelJob() {
            putValue(NAME, "Cancel");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Job job = getLookup().lookup(Job.class);
            final MGXMaster m = getLookup().lookup(MGXMaster.class);

            SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    m.Job().cancel(job);
                    return null;
                }
            };
            sw.execute();
            try {
                sw.get();
            } catch (InterruptedException | ExecutionException ex) {
                NotifyDescriptor nd = new NotifyDescriptor("Could not cancel job.",
                        "Job cancellation failed", NotifyDescriptor.OK_CANCEL_OPTION,
                        NotifyDescriptor.ERROR_MESSAGE, null, null);
                DialogDisplayer.getDefault().notifyLater(nd);
            }
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && RBAC.isUser()
                    && !(getContent().getStatus().equals(JobState.FINISHED) || getContent().getStatus().equals(JobState.FAILED));
        }
    }
}
