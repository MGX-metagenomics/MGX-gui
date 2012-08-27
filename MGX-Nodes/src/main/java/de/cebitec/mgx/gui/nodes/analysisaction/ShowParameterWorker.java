package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Store;
import de.cebitec.mgx.gui.wizard.configurations.data.util.Transform;
import de.cebitec.mgx.gui.wizard.configurations.progressscreen.ProgressBar;
import de.cebitec.mgx.gui.wizard.configurations.utilities.MenuStatus;
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

    /**
     * Tool, dessen Parameter angezeigt werden sollen.
     */
    private Tool tool;
    /**
     * Controller fuer die Anzeige der Parameter.
     */
    private WizardController startUp;
    /**
     * Masterobjekt fuer die Verbindung zum Server.
     */
    private MGXMaster master;
    /**
     * Der Typ des Tools.
     */
    private ToolType toolType;
    /**
     * Der Sequenzierlauf.
     */
    private SeqRun seqRun;
    /**
     * Parameterliste 
     */
    private Collection<JobParameter> parameterList;
    /**
     * @param lTool Das Tool, welches angezeigt werden soll.
     * @param master Objekt fuer die Verbindung zum Server.
     * @param lSeqRun Sequenzierlauf.
     * @param toolType Der Typ des Tools.
     */
    public ShowParameterWorker(Tool lTool, MGXMaster master,
            SeqRun lSeqRun,
            ToolType toolType) {
        this.master = master;
        tool = lTool;
        this.startUp = new WizardController();
        this.toolType = toolType;
        seqRun = lSeqRun;
        parameterList = null;
    }

    /**
     * Methode die im Hintergrund agbearbeitet wird.
     * @return Collection der Parameter.
     */
    @Override
    protected Collection<JobParameter> doInBackground() {
        ProgressBar progress = new ProgressBar("Loading Parameters.",
                "Waiting for the server",
                300, 140);
        switch (toolType) {
            case GLOBAL:
                parameterList = master.Tool().getAvailableParameters(tool.getId(), true);
                break;
            case PROJECT:
                parameterList = master.Tool().getAvailableParameters(tool.getId(), false);
                break;
            case USER_PROVIDED:
                parameterList = master.Tool().getAvailableParameters(tool);
                break;
            default:
                assert false;
        }
        progress.setUpdateText("Loading Project Files");
        List<DirEntry> files = master.File().fetchall();
        progress.dispose();
        Store store = null;

        if (parameterList.size() > 0) {
            store = Transform.getFromJobParameterNodeStore(
                    new ArrayList<>(parameterList));
            store = startUp.startParameterConfiguration(
                    store, files, tool.getName());
            if (startUp.getStatus() == MenuStatus.FINISH) {
                parameterList = Transform.getFromNodeStoreJobParameter(store);
            }
        }

        return parameterList;
    }

    /**
     * Methode die vor dem Beenden des Workers aufgerufen wird.
     */
    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (startUp.getStatus() == MenuStatus.AGAIN && parameterList.size() > 0 
                && startUp.getStatus() != MenuStatus.CANCEL) {
            GetToolsWorker worker = new GetToolsWorker(master, seqRun);
            worker.execute();
        } else if (startUp.getStatus() != MenuStatus.AGAIN &&
                startUp.getStatus() != MenuStatus.CANCEL) {
            String message = null;
            if (parameterList.isEmpty()) {
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
                JobWorker worker = new JobWorker(toolType, tool,
                        new ArrayList(parameterList), master, seqRun);
                worker.execute();
            } else {
                GetToolsWorker worker =
                        new GetToolsWorker( master, seqRun);
                worker.execute();
            }
        }
    }
}
