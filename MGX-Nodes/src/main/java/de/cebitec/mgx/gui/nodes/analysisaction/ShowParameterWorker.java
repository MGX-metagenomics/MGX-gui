package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Store;
import de.cebitec.mgx.gui.wizard.configurations.data.util.Transform;
import de.cebitec.mgx.gui.wizard.configurations.progressscreen.ProgressBar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * ShowParameterWorker fuer die Anzeige von Parametern.
 *
 * @author pbelmann
 */
public class ShowParameterWorker extends SwingWorker<Collection<JobParameter>, Void> {

    private Tool tool;
    private WizardController startUp;
    private MGXMaster master;
    private ToolType toolType;
    private SeqRun seqRun;
    private Collection<JobParameter> list = null;

    public ShowParameterWorker(Tool lTool, WizardController startUp, MGXMaster master,
            SeqRun lSeqRun,
            ToolType toolType) {
        this.master = master;
        tool = lTool;
        this.startUp = startUp;
        this.toolType = toolType;
        seqRun = lSeqRun;
    }

    @Override
    protected Collection<JobParameter> doInBackground() {
        ProgressBar progress = new ProgressBar("Loading Parameters.", 
                "Waiting for the server",
                300, 140);
        switch (toolType) {
            case GLOBAL:
                list = master.Tool().getAvailableParameters(tool.getId(), true);
                break;
            case PROJECT:
                list = master.Tool().getAvailableParameters(tool.getId(), false);
                break;
            case USER_PROVIDED:
                list = master.Tool().getAvailableParameters(tool);
                break;
            default:
                assert false;
        }
        progress.setUpdateText("Loading Project Files");
        List<DirEntry> files = master.File().fetchall();
         progress.dispose();
         Store store = null;
       
        if (list.size() > 0) {
            store = Transform.getFromJobParameterNodeStore(
                    new ArrayList<JobParameter>(list));
            store = startUp.startParameterConfiguration(
                    store, files, tool.getName());
            if (startUp.getStatus() == 1) {
                list = Transform.getFromNodeStoreJobParameter(store);
            }
        }
        
        return list;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (startUp.getStatus() == 2) {

            GetToolsWorker worker = new GetToolsWorker(startUp, master, seqRun);
            worker.execute();

        } else if (startUp.getStatus() == 1) {

            String message = null;
            if (list.isEmpty()) {
                message = "The tool \"" + tool.getName()
                        + "\" has no parameters.\n" + "The tool should be executed?";
            } else {
                message = "The tool \"" + tool.getName()
                        + "\" should be executed?";
            }

            Object[] options = {"Yes",
                "No",};
            int value = JOptionPane.showOptionDialog(null,
                    message, "Tool Installation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null,
                    options, options[0]);
            if (value == JOptionPane.YES_OPTION) {
                JobWorker worker = new JobWorker(startUp, toolType, tool,
                        new ArrayList(list), master, seqRun);
                worker.execute();
            } else {
                GetToolsWorker worker =
                        new GetToolsWorker(startUp, master, seqRun);
                worker.execute();
            }
        }
    }
}
