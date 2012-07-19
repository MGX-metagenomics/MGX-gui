package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.wizard.configurations.action.MenuAction;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Store;
import de.cebitec.mgx.gui.wizard.configurations.data.util.Transform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * ShowParameterWorker fuer die Anzeige von Parametern.
 *
 * @author pbelmann
 */
public class ShowParameterWorker extends SwingWorker<List<JobParameter>, Void> {

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

    public ShowParameterWorker(Tool lTool, WizardController startUp, MGXMaster master, SeqRun seqrun) {
        this.master = master;
        tool = lTool;
        this.startUp = startUp;
        this.seqrun = seqrun;
    }

    @Override
    protected List<JobParameter> doInBackground() {

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
        List<JobParameter> list = new ArrayList<>();
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

        LOGGER.log(Level.INFO, "JobParameter: {0}", list.size());
        Store store = Transform.getFromJobParameterNodeStore(list);
        List<DirEntry> fetchall = master.File().fetchall();
        LOGGER.info("after setStore");
        if (list.size() > 0) {
            this.store = startUp.startParameterConfiguration(store, fetchall,
                    tool.getName());
        }
        return list;
    }

    @Override
    protected void done() {
        LOGGER.info("DONE SHOWTOOLWORKER");
        List<JobParameter> list = null;
        try {
            list = get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (list.size() > 0) {
            if (startUp.getStatus() == MenuAction.again) {
                LOGGER.info("Again SHOWTOOLWORKER");
                GetToolsWorker worker = new GetToolsWorker(startUp, master,
                        seqrun);
                worker.execute();
            } else if (startUp.getStatus() == MenuAction.finish) {
                jobParameterList =
                        Transform.getFromNodeStoreJobParameter(store);
                JobWorker worker = new JobWorker(jobParameterList, jobid, job,
                        master, startUp, seqrun);
                worker.execute();
                LOGGER.info("Finish SHOWTOOLWORKER");
            } else if (startUp.getStatus() == MenuAction.cancel) {
                LOGGER.info("Cancel SHOWTOOLWORKER");
            }
        } else {

            Object[] options = {"Yes",
                "No",};
            int value = JOptionPane.showOptionDialog(null,
                    "The tool has no configurable parameters. "
                    + "Do you want to execute the tool?",
                    "Tool Execution",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null,
                    options, options[0]);
            if (value == JOptionPane.YES_OPTION) {
                jobParameterList = new ArrayList<>();
                JobWorker worker = new JobWorker(jobParameterList, jobid, job,
                        master, startUp, seqrun);
                worker.execute();
            } else if (value == JOptionPane.NO_OPTION) {
                GetToolsWorker worker = new GetToolsWorker(startUp, master, seqrun);
                worker.execute();
            }
        }
    }
}
