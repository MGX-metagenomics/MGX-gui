/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import de.cebitec.mgx.gui.wizard.configurations.progressscreen.ProgressBar;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 * JobWorker, der den Job an den Server sendet.
 *
 * @author pbelmann
 */
public class JobWorker extends SwingWorker {

    private final static Logger LOGGER =
            Logger.getLogger(LocalToolWorker.class.getName());
    /**
     * ParameterListe, die an den Server gesendet wird.
     */
    private final List<JobParameter> jobParameterList;
    /**
     * Job Id des jobs.
     */
    private long jobId;
    /**
     * Job mit dem ausgewaehlten und bearbeiteten tool.
     */
    private Job job;
    /**
     * Master objekt. der den Server aufruft.
     */
    private MGXMaster master;
    /**
     * GuiController.
     */
    private WizardController startup;
    private SeqRun run;

    /**
     * Konstruktor
     *
     * @param lParameter Parameter
     * @param lJobId JobId
     * @param lJob Job mit dem Tool
     * @param lMaster Masterobjekt.
     * @param lStartup GuiController
     */
    public JobWorker(List<JobParameter> lParameter, long lJobId, Job lJob, 
            MGXMaster lMaster, WizardController lStartup, SeqRun lRun) {
        run = lRun;
        jobParameterList = lParameter;
        jobId = lJobId;
        job = lJob;
        master = lMaster;
        startup = lStartup;
    }

    /**
     * Methode wird im Hintergrund bearbeitet.
     *
     * @return
     */
    @Override
    protected Object doInBackground() {
        LOGGER.info("jobWorker");
        String parameter = "";
        // muster: nodeid.configname "wert"

        if (jobParameterList != null) {
            for (JobParameter jobParameter : jobParameterList) {
                String answer = jobParameter.getConfigItemValue().replaceAll("\n", " ");
                answer = answer.replaceAll("\r\n", " ");

                parameter = parameter + jobParameter.getNodeId() + "."
                        + jobParameter.getConfigItemName() + "\""
                        + answer + "\"";
            }
            job.setParameters(parameter);
            
            
            
            try {
                master.Job().setParameters(jobId, jobParameterList);
            } catch (MGXServerException ex) {
                Exceptions.printStackTrace(ex);
            }

            boolean job_is_ok = false;
            try {
                
                ProgressBar bar = new ProgressBar("Verifying tool parameters.",
                        "Waiting for the server",
                        300, 140);
                job_is_ok = master.Job().verify(jobId);

                if (job_is_ok) {
                    LOGGER.info("job is ok");
                    bar.setUpdateText("Parameters are fine. Running tool.");
                    master.Job().execute(jobId);
                    bar.dispose();
                }

            } catch (MGXServerException ex) {
                Exceptions.printStackTrace(ex);
            }
            Object[] options = {"Yes",
                "No",};
            int value = JOptionPane.showOptionDialog(null,
                    "Tool is executed. Do you want to return to the tool overview.", 
                    "Tool Execution.",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null,
                    options, options[0]);
            if (value == JOptionPane.YES_OPTION) {
                GetToolsWorker worker = new GetToolsWorker(startup, master, run);
                worker.execute();
            } else if (value == JOptionPane.NO_OPTION) {
            }
        }
        return null;
    }

    /**
     * Methode wird aufgerufen, bevor der Worker beendet wird.
     */
    @Override
    protected void done() {
        LOGGER.info("jobWorker finish");
    }
}
