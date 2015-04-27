package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.FileType;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.misc.TaskI.State;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.JobState;
import static de.cebitec.mgx.api.model.JobState.FAILED;
import static de.cebitec.mgx.api.model.JobState.FINISHED;
import static de.cebitec.mgx.api.model.JobState.RUNNING;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.swingutils.util.FileChooserUtils;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
public class JobNode extends MGXNodeBase<JobI> {

    public static String TOOL_PROPERTY = "tool";
    public static String SEQRUN_PROPERTY = "seqrun";
    public static String STATE_PROPERTY = "state";
    //private final Job job;

    public JobNode(JobI job, Children c) {
        super(job.getMaster(), Children.LEAF, Lookups.fixed(job.getMaster(), job), job);
        ToolI tool = job.getTool();
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

    private String getParameterToolTip(JobI job) {
        if (job.getParameters() == null || job.getParameters().isEmpty()) {
            return "";
        }

        // FIXME: handle MGXFile
        StringBuilder sb = new StringBuilder();
        for (JobParameterI jp : job.getParameters()) {
            //jp.getMaster().log(Level.INFO, jp.getType());
            String paramValue = jp.getParameterValue();
            if (jp.getType().equals("ConfigMGXReference")) {
                try {
                    MGXReferenceI reference = job.getMaster().Reference().fetch(Long.parseLong(jp.getParameterValue()));
                    paramValue = reference.getName();
                } catch (MGXException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            sb.append(jp.getParameterName())
                    .append(": ")
                    .append(paramValue)
                    .append("<br>");
        }
        return sb.toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<>(5);
        actions.add(new DeleteJob());
        actions.add(new GetError());
        actions.add(new ResubmitAction());
        actions.add(new CancelJob());
        actions.add(new SaveToolXML());

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
            final JobI job = getLookup().lookup(JobI.class);
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
                        MGXMasterI m = getLookup().lookup(MGXMasterI.class);
                        TaskI<JobI> task;
                        try {
                            task = m.Job().delete(job);
                        } catch (MGXException ex) {
                            setStatus(ex.getMessage());
                            failed();
                            return false;
                        }
                        while (!task.done()) {
                            setStatus(task.getStatusMessage());
                            try {
                                m.<JobI>Task().refresh(task);
                            } catch (MGXException ex) {
                                setStatus(ex.getMessage());
                                failed();
                                return false;
                            }
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
            final JobI job = getLookup().lookup(JobI.class);
            final MGXMasterI m = getLookup().lookup(MGXMasterI.class);

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
            final JobI job = getLookup().lookup(JobI.class);
            final MGXMasterI m = getLookup().lookup(MGXMasterI.class);

            final MGXTask restartTask = new MGXTask("Restart " + job.getTool().getName()) {
                @Override
                public boolean process() {
                    setStatus("Restarting job..");
                    TaskI<JobI> task = null;
                    try {
                        task = m.Job().restart(job);
                    } catch (MGXException ex) {
                        setStatus(ex.getMessage());
                        failed();
                        return false;
                    }
                    while (task != null && !task.done()) {
                        setStatus(task.getStatusMessage());
                        try {
                            m.<JobI>Task().refresh(task);
                        } catch (MGXException ex) {
                            setStatus(ex.getMessage());
                            failed();
                            return false;
                        }
                        sleep();
                    }
                    if (task != null) {
                        task.finish();
                        job.modified();
                        return task.getState() == TaskI.State.FINISHED;
                    }
                    return false;
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
            final JobI job = getLookup().lookup(JobI.class);
            final MGXMasterI m = getLookup().lookup(MGXMasterI.class);

            SwingWorker<Boolean, Void> sw = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    return m.Job().cancel(job);
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

    private class SaveToolXML extends AbstractAction {

        public SaveToolXML() {
            putValue(NAME, "Download workflow");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final JobI job = getLookup().lookup(JobI.class);
            final MGXMasterI m = getLookup().lookup(MGXMasterI.class);

            final String fname = FileChooserUtils.selectNewFilename(new FileType[]{FileType.XML}, job.getTool().getName() + "-" + job.getTool().getVersion());
            if (fname == null || fname.trim().isEmpty()) {
                return;
            }

            SwingWorker<String, Void> sw = new SwingWorker<String, Void>() {

                @Override
                protected String doInBackground() throws Exception {
                    return m.Tool().getXMLDefinition(job.getTool());
                }

                @Override
                protected void done() {
                    super.done();
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(fname))) {
                        bw.write(get());
                    } catch (Exception ex) {
                        NotifyDescriptor nd = new NotifyDescriptor.Message("Error: " + ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                        DialogDisplayer.getDefault().notify(nd);
                        return;
                    }
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Workflow saved to " + fname, NotifyDescriptor.INFORMATION_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                }

            };
            sw.execute();
        }
    }
}
