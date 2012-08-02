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
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 * JobWorker, der den Job an den Server sendet.
 *
 * @author pbelmann
 */
public class JobWorker extends SwingWorker<Void, Void> {

    private final List<JobParameter> jobParameterList;
    private Job job;
    private MGXMaster master;

    /**
     * Konstruktor
     *
     * @param lParameter Parameter
     * @param lJob Job mit dem Tool
     * @param lMaster Masterobjekt.
     */
    public JobWorker(List<JobParameter> lParameter, Job lJob, MGXMaster lMaster) {
        jobParameterList = lParameter;
        job = lJob;
        master = lMaster;
    }

    /**
     * Methode wird im Hintergrund bearbeitet.
     *
     * @return
     */
    @Override
    protected Void doInBackground() {
        if ((jobParameterList != null) && jobParameterList.size() > 0){
            try {
                job.setParameters(jobParameterList);
                master.Job().setParameters(job.getId(), jobParameterList);
            } catch (MGXServerException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
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
    }
}
