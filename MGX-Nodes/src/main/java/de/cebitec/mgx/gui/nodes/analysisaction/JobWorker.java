
package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.JobState;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.progressscreen.ProgressBar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 * JobWorker, der den Job an den Server uebergibt.
 *
 * @author pbelmann
 */
public class JobWorker extends SwingWorker<Void, Void> {

    private final List<JobParameter> jobParameterList;
    private final MGXMaster master;
    private final ToolType toolType;
    private Tool tool;
    private final SeqRun seqRun;

    /**
     * Konstruktor fuer den Worker.
     *
     * @param lParameter Parameter
     * @param lJob Job mit dem Tool
     * @param lMaster Masterobjekt.
     */
    public JobWorker(ToolType lToolType, Tool lTool, List<JobParameter> lParameter,
            MGXMaster lMaster, SeqRun lSeqrun) {
        toolType = lToolType;
        tool = lTool;
        jobParameterList = lParameter;
        master = lMaster;
        seqRun = lSeqrun;
    }

    /**
     * Methode wird im Hintergrund bearbeitet.
     *
     * @return
     */
    @Override
    protected Void doInBackground() {

        ProgressBar progress = new ProgressBar("Executing tool.",
                "Waiting for the server",
                300, 140);
        switch (toolType) {
            case GLOBAL:
                Long installedToolId = null;
                try {
                    installedToolId = master.Tool().installTool(tool.getId());
                } catch (MGXServerException ex) {
                    Exceptions.printStackTrace(ex);
                }
                tool = master.Tool().fetch(installedToolId);
                break;
            case USER_PROVIDED:
                long toolId = master.Tool().create(tool);
                break;
        }

        Job job = new Job();
        job.setTool(tool);
        job.setSeqrun(seqRun);
        job.setCreator(master.getLogin());
        job.setStatus(JobState.CREATED);
        job.setParameters(jobParameterList);
        Long job_id = master.Job().create(job);
        boolean job_ok = false;
        progress.setUpdateText("Verifying..");

        try {
            job_ok = master.Job().verify(job);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }

        progress.setUpdateText("Submitting..");

        try {
            master.Job().execute(job);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        progress.dispose();
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
