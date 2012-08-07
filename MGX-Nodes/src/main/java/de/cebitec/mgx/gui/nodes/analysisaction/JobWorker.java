/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.JobState;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import de.cebitec.mgx.gui.wizard.configurations.progressscreen.ProgressBar;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 * JobWorker, der den Job an den Server sendet.
 *
 * @author pbelmann
 */
public class JobWorker extends SwingWorker<Void, Void> {

    private final List<JobParameter> jobParameterList;
    private MGXMaster master;
    private ToolType toolType;
    private WizardController startUp;
    private Tool tool;
    private SeqRun seqRun;

    /**
     * Konstruktor
     *
     * @param lParameter Parameter
     * @param lJob Job mit dem Tool
     * @param lMaster Masterobjekt.
     */
    public JobWorker(WizardController lStartUp, ToolType lToolType, Tool lTool, List<JobParameter> lParameter,
            MGXMaster lMaster, SeqRun lSeqrun) {
        toolType = lToolType;
        tool = lTool;
        jobParameterList = lParameter;
        master = lMaster;
        seqRun = lSeqrun;
        startUp = lStartUp;
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

        Long job_id = null;
        switch (toolType) {
            case PROJECT:
                Job job = new Job();
                job.setTool(tool);
                job.setSeqrun(seqRun);
                job.setCreator(master.getLogin());
                job.setStatus(JobState.CREATED);
                job.setParameters(jobParameterList);
                job_id = master.Job().create(job);
//                 if ((jobParameterList != null) && jobParameterList.size() > 0){
//            try {
//                master.Job().setParameters(job_id, jobParameterList);
//            } catch (MGXServerException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//            }
                break;
            case GLOBAL:
                Long installedToolId = null;
                try {
                    installedToolId = master.Tool().installTool(tool.getId());
                } catch (MGXServerException ex) {
                    Exceptions.printStackTrace(ex);
                }
                job = new Job();
                job.setTool(tool);
                job.setSeqrun(seqRun);
                job.setCreator(master.getLogin());
                job.setStatus(JobState.CREATED);
                job.setParameters(jobParameterList);
                job_id = master.Job().create(job);
//                 if ((jobParameterList != null) && jobParameterList.size() > 0){
//            try {
//                master.Job().setParameters(job.getId(), jobParameterList);
//            } catch (MGXServerException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//            }
                break;
            case USER_PROVIDED:
                master.Tool().create(tool);
                job = new Job();
                job.setTool(tool);
                job.setSeqrun(seqRun);
                job.setCreator(master.getLogin());
                job.setStatus(JobState.CREATED);
                job.setParameters(jobParameterList);
                job_id = master.Job().create(job);

        }

        boolean job_ok = false;

        progress.setUpdateText("Verifying Parameters.");

        try {
            job_ok = master.Job().verify(job_id);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }

        System.err.println("job verification: " + job_ok);

        boolean submitted = false;

        progress.setUpdateText("Executing Parameters.");

        try {
            submitted = master.Job().execute(job_id);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        System.err.println("job execution: " + submitted);
        progress.dispose();
        return null;
    }

    /**
     * Methode wird aufgerufen, bevor der Worker beendet wird.
     */
    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }

        Object[] options = {"Yes",
            "No",};
        int value = JOptionPane.showOptionDialog(null,
                "The tool has been successfully installed.\n"
                + "Do you want to return back to the tool Overview?",
                "",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);
        if (value == JOptionPane.YES_OPTION) {
            GetToolsWorker worker =
                    new GetToolsWorker(startUp, master, seqRun);
            worker.execute();
        } else {
        }
    }
}
