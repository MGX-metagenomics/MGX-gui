package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.CancelJob;
import de.cebitec.mgx.gui.actions.SaveToolXML;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.common.JobState;
import java.awt.Image;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.Action;
import org.openide.filesystems.FileUtil;
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
    public static String SEQRUN_OR_ASSEMBLY_PROPERTY = "seqrunOrAssembly";
    public static String STATE_PROPERTY = "state";

    public JobNode(JobI job) {
        super(Children.LEAF, Lookups.fixed(job.getMaster(), job), job);
        ToolI tool = job.getTool();
        super.setDisplayName(tool.getName());
        String shortDesc = new StringBuilder("<html>[")
                .append(job.getId())
                .append("]<b>&nbsp;")
                .append(tool.getName()).append("</b>")
                .append("<br><hr><br>")
                .append("Tool version: ")
                .append(tool.getVersion())
                .append("<br>")
                .append(tool.getDescription())
                .append("<br><br>")
                .append("Job created by: ")
                .append(job.getCreator())
                .append("<br><br>Start time: ")
                .append(job.getStartDate() != null ? job.getStartDate() : "n/a")
                .append("<br>Finish time: ")
                .append(job.getStatus() == JobState.FINISHED ? job.getFinishDate() : "n/a")
                .append("<br>")
                .append(getProcessingTime(job))
                .append("<br>")
                .append(getParameterToolTip(job))
                .append("</html>")
                .toString();
        super.setShortDescription(shortDesc);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/AnalysisTasks.png");
    }

    @Override
    public void destroy() throws IOException {
        System.err.println("JobNode#destroy");
        super.destroy();
    }

    private String getProcessingTime(JobI job) {
        if (job.getStatus() == JobState.FINISHED) {

            long time = getDateDiff(job.getFinishDate(), job.getStartDate(), TimeUnit.MINUTES);

            Duration duration = Duration.ofMinutes(time);
            long hours = duration.toHours();
            long mins = duration.minusHours(hours).toMinutes();
            String formatted = String.format("%dhrs %02dmins", hours, mins);
            
            return "Processing time: " + formatted + "<br>";
        } else {
            return "";
        }
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date1.getTime() - date2.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    private String getParameterToolTip(JobI job) {
        if (job.getParameters() == null || job.getParameters().isEmpty()) {
            return "";
        }

        // FIXME: handle MGXFile
        StringBuilder sb = new StringBuilder();
        for (JobParameterI jp : job.getParameters()) {
            String paramValue = jp.getParameterValue();
            if (jp.getType().equals("ConfigMGXReference") && jp.getParameterValue() != null) {
                try {
                    MGXReferenceI reference = job.getMaster().Reference().fetch(Long.parseLong(jp.getParameterValue()));
                    paramValue = reference.getName();
                } catch (MGXException ex) {
                    if (ex.getMessage().contains("No object of type Reference for ID")) {
                        paramValue = "(deleted)";
                    } else {
                        Exceptions.printStackTrace(ex);
                        paramValue = jp.getParameterValue();
                    }
                } catch (NumberFormatException nfe) {
                    paramValue = jp.getParameterValue();
                }
            }
            sb.append(jp.getUserName())
                    .append(": ")
                    .append(paramValue)
                    .append("<br>");
        }
        return sb.toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        Action deleteJob = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-DeleteJobNodeAction.instance", Action.class);
        Action getError = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-ShowError.instance", Action.class);
        Action restartJob = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-RestartJobAction.instance", Action.class);
        return new Action[]{deleteJob, getError, restartJob, new CancelJob(), new SaveToolXML()};
    }

    @Override
    protected Sheet createSheet() {

        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();

        Property<?> toolProperty = new PropertySupport.ReadOnly<String>(TOOL_PROPERTY, String.class, "Tool", "Tool name") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return getContent().getTool().getName();
            }
        };
        toolProperty.setValue("suppressCustomEditor", Boolean.TRUE);
        set.put(toolProperty);

        Property<?> runProperty = new PropertySupport.ReadWrite<String>(SEQRUN_OR_ASSEMBLY_PROPERTY, String.class, "Run", "Run name") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (getContent().getSeqruns() != null && getContent().getSeqruns().length > 0) {
                    String[] elems = new String[getContent().getSeqruns().length];
                    for (int i = 0; i < elems.length; i++) {
                        elems[i] = getContent().getSeqruns()[i].getName();
                    }
                    return String.join(", ", elems);
                } else if (getContent().getAssembly() != null) {
                    return getContent().getAssembly().getName();
                } else {
                    return "";
                }
            }

            @Override
            public void setValue(String t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            }
        };
        runProperty.setValue("suppressCustomEditor", Boolean.TRUE);
        set.put(runProperty);

        Property<?> stateProperty = new PropertySupport.ReadWrite<String>(STATE_PROPERTY, String.class, "State", "Job state") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                JobState state = getContent().getStatus();
                String icon = null;
                switch (state) {
                    case FINISHED:
                        icon = "de/cebitec/mgx/gui/nodes/JobStateOK.svg";
                        break;
                    case FAILED:
                        icon = "de/cebitec/mgx/gui/nodes/JobStateFAILED.svg";
                        break;
                    case RUNNING:
                        icon = "de/cebitec/mgx/gui/nodes/JobStateRUNNING.svg";
                        break;
                }
                if (icon != null) {
                    Image image = ImageUtilities.loadImage(icon, true);
                    Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                    setValue("valueIcon", scaledInstance);
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
        ToolI tool = getContent().getTool();
        String shortDesc = new StringBuilder("<html><b>")
                .append(tool.getName()).append("</b>")
                .append("<br><hr><br>")
                .append("Tool version: ")
                .append(tool.getVersion())
                .append("<br>")
                .append(tool.getDescription())
                .append("<br><br>")
                .append("Job created by: ")
                .append(getContent().getCreator())
                .append("<br><br>Start time: ")
                .append(getContent().getStartDate() != null ? getContent().getStartDate() : "n/a")
                .append("<br>Finish time: ")
                .append(getContent().getStatus() == JobState.FINISHED ? getContent().getFinishDate() : "n/a")
                .append("<br>")
                .append(getProcessingTime(getContent()))
                .append(getParameterToolTip(getContent()))
                .append("</html>")
                .toString();
        super.setShortDescription(shortDesc);
        // job state might have changed
        fireIconChange();
    }

}
