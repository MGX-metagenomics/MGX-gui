package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.JobState;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class JobNode extends MGXNodeBase<Job> {

    public static String TOOL_PROPERTY = "tool";
    public static String SEQRUN_PROPERTY = "seqrun";
    public static String STATE_PROPERTY = "state";
    private final Job job;

    public JobNode(MGXMaster m, Job job, Children c) {
        super(Children.LEAF, Lookups.fixed(m, job), job);
        this.job = job;
        setDisplayName(job.getTool().getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/AnalysisTasks.png");
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new DeleteJob()};
    }

    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();

        Property toolProperty = new PropertySupport.ReadOnly<String>(TOOL_PROPERTY, String.class, "Tool", "Tool name") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return job.getTool().getName();
            }
        };
        toolProperty.setValue("suppressCustomEditor", Boolean.TRUE);
        set.put(toolProperty);

        Property runProperty = new PropertySupport.ReadWrite<String>(SEQRUN_PROPERTY, String.class, "Run", "Run name") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return job.getSeqrun().getName();
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
                return job.getStatus().toString();
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
        setDisplayName(job.getTool().getName());
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
                MGXTask deleteTask = new MGXTask("Delete " + job.getTool().getName() + " " + job.getSeqrun().getName()) {
                    @Override
                    public void process() {
                        setStatus("Deleting..");
                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                        m.Job().delete(job);
                    }

                    @Override
                    public boolean isDeterminate() {
                        return false;
                    }

                    @Override
                    public int getProgress() {
                        return MGXTask.PROGRESS_UNKNOWN;
                    }
                };

                TaskManager.getInstance().addTask(deleteTask);

            }
        }

        @Override
        public boolean isEnabled() {
            return super.isEnabled() && RBAC.isUser() && !job.getStatus().equals(JobState.RUNNING);
        }
    }
}
