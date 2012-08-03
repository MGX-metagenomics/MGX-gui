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
   
    private MGXMaster master;
    private ToolType toolType;
    private Tool tool;
    private SeqRun seqRun;   
    
    /**
     * Konstruktor
     *
     * @param lParameter Parameter
     * @param lJob Job mit dem Tool
     * @param lMaster Masterobjekt.
     */
    public JobWorker(ToolType lToolType ,Tool lTool,List<JobParameter> lParameter,  
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
        switch(toolType){
            case PROJECT:
                Job job = new Job();
                job.setTool(tool);
                job.setId(tool.getId());
                job.setSeqrun(seqRun);
                job.setCreator(master.getLogin());
                job.setStatus(JobState.CREATED);  
                job.setParameters(jobParameterList);
                Long job_id = master.Job().create(job);
                 if ((jobParameterList != null) && jobParameterList.size() > 0){
            try {
                master.Job().setParameters(job.getId(), jobParameterList);
            } catch (MGXServerException ex) {
                Exceptions.printStackTrace(ex);
            }
            }
                boolean job_ok = false;
            try {
                job_ok = master.Job().verify(job_id);
            } catch (MGXServerException ex) {
                Exceptions.printStackTrace(ex);
            }
            
            System.err.println("job verification: " + job_ok);
            
            boolean submitted = false;
            try {
                submitted = master.Job().execute(job_id);
            } catch (MGXServerException ex) {
                Exceptions.printStackTrace(ex);
            }
            
            System.err.println("job execution: " + submitted);
        
                break;
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
