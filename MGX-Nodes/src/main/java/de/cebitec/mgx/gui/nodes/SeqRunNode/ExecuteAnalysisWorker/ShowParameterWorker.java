/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes.SeqRunNode.ExecuteAnalysisWorker;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.nodes.SeqRunNode.SeqRunNode;
import de.cebitec.mgx.gui.wizard.configurations.action.MenuAction;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Store;
import de.cebitec.mgx.gui.wizard.configurations.data.util.Transform;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;
import org.openide.util.Lookup;

/**
 *
 * ShowParameterWorker fuer die Anzeige von Parametern.
 *
 * @author pbelmann
 */
public class ShowParameterWorker extends SwingWorker {

    private final static Logger LOGGER =
            Logger.getLogger(ShowParameterWorker.class.getName());
    Tool tool;
    boolean globalToolIsPresent = false;
    private long project_tool_id;
    private Job job;
    private long jobid;
    private List<JobParameter> jobParameterList;
    private SeqRun seqrun;
    private Store store;
    private WizardController startUp;
    private MGXMaster master;
    private ArrayList<JobParameter> list;

    public ShowParameterWorker(Tool lTool,
            WizardController startUp, MGXMaster master, SeqRun seqrun) {
        this.master = master;
        tool = lTool;
        this.startUp = startUp;
        this.seqrun = seqrun;
    }

    @Override
    protected Object doInBackground() {

//        master.Tool().delete(tool.getId());

        job = new Job();
        job.setSeqrun(seqrun);
        job.setCreator(master.getLogin());
        job.setTool(tool);
//                    tool.setId(project_tool_id);

        LOGGER.info("Before create JobID: " + jobid);

        jobid = master.Job().create(job);

        LOGGER.info("After create JobID: " + jobid);
        jobParameterList = null;
        //parsen
        list = new ArrayList<JobParameter>();
        Iterator iterator = null;

        try {
            iterator = master.Job().getParameters(jobid).iterator();
            LOGGER.info("After Get Parameters");
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (!iterator.hasNext()) {
            LOGGER.info("Iterator has no next");
        }

        while (iterator.hasNext()) {
            list.add((JobParameter) iterator.next());
        }

        LOGGER.info("JobParameter: " + list.size());
        Store store = Transform.getFromJobParameterNodeStore(list);
        List<DirEntry> fetchall = master.File().fetchall();
        LOGGER.info("after setStore");
        if (list.size() > 0) {
            this.store = startUp.startParameterConfiguration(store, fetchall,
                    tool.getName());
        }
        return null;
    }

    @Override
    protected void done() {
        LOGGER.info("DONE SHOWTOOLWORKER");
        if (list.size() > 0) {
            if (startUp.getStatus() == MenuAction.again) {
                LOGGER.info("Again SHOWTOOLWORKER");
                GetToolsWorker worker = new GetToolsWorker(startUp, master, 
                        seqrun);
                worker.execute();
            } else if (startUp.getStatus() == MenuAction.finish) {
                jobParameterList = new ArrayList<JobParameter>();
                jobParameterList =
                        Transform.getFromNodeStoreJobParameter(store);
                JobWorker worker = new JobWorker(jobParameterList, jobid, job, 
                        master, startUp, seqrun);
                worker.execute();
                LOGGER.info("Finish SHOWTOOLWORKER");
            } else if (startUp.getStatus() == MenuAction.cancel) {
                LOGGER.info("Cancel SHOWTOOLWORKER");
            }
        }
    }
}
