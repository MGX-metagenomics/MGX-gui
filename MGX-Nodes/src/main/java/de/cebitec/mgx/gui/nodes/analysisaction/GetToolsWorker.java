package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import de.cebitec.mgx.gui.wizard.configurations.progressscreen.ProgressBar;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * Ist fuer das Beschaffen der Tools zustaendig, die Angezeigt werden sollen.
 * 
 * 
 * @author pbelmann
 */
public class GetToolsWorker extends SwingWorker<Void, Void> {

    /**
     * Das Tool, welches ausgesucht wurde.
     */
    private Tool tool;
    /**
     * Globale Tools.
     */
    private List<Tool> globalTools;
    /**
     * Projekt Tools.
     */
    private List<Tool> projectTools;
    /**
     * Tooltypen.
     */
    private ToolType toolType;
    /**
     * Der GuiController.
     */
    private WizardController startUp;
    /**
     * Das Master Objekt um Verbindung zum Server aufzunehmen.
     */
    private MGXMaster master;
    /**
     * Der Sequenzierlauf.
     */
    private SeqRun seqRun;
    /**
     * Soll das Tool geloescht werden oder nicht.
     */
    private boolean isDelete;

   /**
    * 
    * @param startup 
    * @param lMaster
    * @param lSeqrun 
    */
    public GetToolsWorker(WizardController startup,
            MGXMaster lMaster, SeqRun lSeqrun) {
        startUp = startup;
        master = lMaster;
        seqRun = lSeqrun;
        isDelete = false;
    }

    /**
     * Methode wird im Hintergrund bearbeitet.
     * @return null
     */
    @Override
    protected Void doInBackground() {
        ProgressBar progress = new ProgressBar("Loading tools.",
                "Loading global tools.",
                300, 140);

        try {
            globalTools = new ArrayList<Tool>();
            globalTools = master.Tool().listGlobalTools();
            progress.setUpdateText("Loading project tools.");
            projectTools = master.Tool().fetchall();
            progress.dispose();
            tool = startUp.startToolViewStartUp(globalTools, projectTools);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }
        isDelete = startUp.isToolDelete();
        toolType = startUp.getLastToolType();

        return null;
    }

    @Override
    protected void done() {
        if (tool != null) {
            if (isDelete) {
                DeleteWorker deleteWorker =
                        new DeleteWorker(startUp, master, tool, seqRun);
                deleteWorker.execute();
            } else {
                ShowParameterWorker worker =
                        new ShowParameterWorker(tool, startUp, master, seqRun, toolType);
                worker.execute();
            }
        }
    }
}
