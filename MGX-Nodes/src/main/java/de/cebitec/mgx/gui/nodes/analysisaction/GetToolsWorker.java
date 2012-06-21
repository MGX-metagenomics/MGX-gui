package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author pbelmann
 */
public class GetToolsWorker extends SwingWorker {

    private final static Logger LOGGER =
            Logger.getLogger(GetToolsWorker.class.getName());
    /**
     * Das Tool, welches ausgesucht wurde.
     */
    private Tool tool;
    /**
     * Globale bzw. auf dem Server liegende Tools.
     */
    private final int GLOBAL = 0;
    /**
     * Lokale auf dem Rechner liegende Tools.
     */
    private final int LOCAL = 1;
    /**
     * Im Projekt liegende Tools.
     */
    private final int PROJECT = 2;
    /**
     * Globale Tools.
     */
    private Collection<Tool> globalTools;
    /**
     * Projekt Tools.
     */
    private List<Tool> projectTools;
    /**
     * Tooltypen.
     */
    private int toolType = 3;
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
    private boolean isDelete = false;

    /**
     * Konstruktor fuer den Worker, der die Tools anzeigt.
     *
     * @param startup GuiController
     * @param master Masterobjekt
     * @param lSeqrun Sequenzierlauf.
     */
    public GetToolsWorker(WizardController startup, MGXMaster master, SeqRun lSeqrun) {
        startUp = startup;
        this.master = master;
        seqRun = lSeqrun;
    }

    /**
     * Methode die im Hintergrund laeuft.
     *
     * @return Objekt.
     */
    @Override
    protected Object doInBackground() {

        toolType = 5;

        try {
            List<Tool> tools = new ArrayList<>();
            globalTools = master.Tool().listGlobalTools();
            projectTools = master.Tool().fetchall();

            LOGGER.info("Globaltools Size: " + globalTools.size());
            LOGGER.info("Projecttools Size: " + projectTools.size());

            tool = startUp.startToolViewStartUp(globalTools, projectTools);
            LOGGER.info("tools set");
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }

        isDelete = startUp.isToolDelete();

        if (globalTools.contains(tool)) {
            LOGGER.info("GlobalTool");

            toolType = GLOBAL;

        } else if (projectTools.contains(tool)) {
            toolType = PROJECT;
            LOGGER.info("ProjectTool");
        } else if (tool == null) {
        } else {
            LOGGER.info("localTool");
            toolType = LOCAL;
        }
        return null;
    }

    /**
     * Die Methode, die beim Beenden des Workers aufgerufen wird.
     */
    @Override
    protected void done() {
        startUp.closeProgressBar();
        LOGGER.info("Get Tools Worker DONE");

        if (isDelete) {
            DeleteWorker deleteWorker = new DeleteWorker(startUp, master, tool.getId(), seqRun);
            deleteWorker.execute();
        } else {

            switch (toolType) {
                case GLOBAL:
                    GlobalToolWorker globalToolWorker =
                            new GlobalToolWorker(tool, startUp,
                            master, seqRun);
                    globalToolWorker.execute();
                    break;
                case LOCAL:

                    Object[] options = {"Yes",
                        "No",};
                    int value = JOptionPane.showOptionDialog(null,
                            "Do you want to install the tool in your project. You can"
                            + " view it then in your project view.", "tool installation",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null,
                            options, options[0]);
                    if (value == JOptionPane.YES_OPTION) {


                        LocalToolWorker localToolWorker =
                                new LocalToolWorker(tool, startUp,
                                master, seqRun);
                        localToolWorker.execute();

                    } else if (value == JOptionPane.NO_OPTION) {
                    }
                    break;
                case PROJECT:
                    ShowParameterWorker showToolsWorker =
                            new ShowParameterWorker(tool, startUp,
                            master, seqRun);
                    showToolsWorker.execute();
                    break;
                default:

            }
        }
        super.done();
    }
}
