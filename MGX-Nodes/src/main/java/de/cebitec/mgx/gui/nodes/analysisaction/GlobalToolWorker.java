/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import de.cebitec.mgx.gui.wizard.configurations.progressscreen.ProgressBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Worker um die Globalen bzw auf dem Server liegenden Tools zu installieren.
 *
 * @author pbelmann
 */
public class GlobalToolWorker extends SwingWorker {

    private final static Logger LOGGER =
            Logger.getLogger(GlobalToolWorker.class.getName());
    /**
     * Tool welches im Projekt installiert werden soll.
     */
    private Tool tool;
    /**
     * Project Tool id.
     */
    private long project_tool_id = -2;
    /**
     * GuiController.
     */
    private WizardController startUp;
    /**
     * Masterobjekt um Verbindung mitt dem Server aufzunehmen.
     */
    private MGXMaster master;
    /**
     * Sequenzierlauf.
     */
    private SeqRun seqRun;
    /**
     * Installierflag.
     */
    private int install;
    private ProgressBar progress;

    /**
     * Konstruktor
     *
     * @param lTool zu installierendes Tool
     * @param startup GuiController
     * @param lMaster Masterobjekt.
     * @param lSeqRun Sequenzierlauf
     */
    public GlobalToolWorker(Tool lTool,
            WizardController startup,
            MGXMaster lMaster, SeqRun lSeqRun) {
        tool = lTool;
        seqRun = lSeqRun;
        startUp = startup;
        master = lMaster;
    }

    /**
     * Methode, die im Hintergrund ausgefuehrt wird.
     *
     * @return Objekt.
     */
    @Override
    protected Object doInBackground() {
        LOGGER.info("GlobalTool");

        Object[] options = {"Yes",
            "No",};
        int value = JOptionPane.showOptionDialog(null,
                "Should the tool \"" + tool.getName() + "\" be installed?\n"
                + "You can choose it then in the project tools overview.", "Tool Installation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);
        if (value == JOptionPane.YES_OPTION) {
            progress = new ProgressBar("Installing tool.","Waiting for the server.",
                    300, 140);
            project_tool_id = 0;
            try {
                project_tool_id = master.Tool().installTool(tool.getId());
            } catch (MGXServerException ex) {

                project_tool_id = 0;
            }
            progress.dispose();

            String[] ok = {"ok"};
            int answer = JOptionPane.showOptionDialog(null,
                    "The tool is installed and available in "
                    + "your project tool overview.",
                    "Tool Installation",
                    JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, ok, "OK");
            if (answer == JOptionPane.OK_OPTION || answer == JOptionPane.CLOSED_OPTION) {
                GetToolsWorker worker = new GetToolsWorker(startUp, master, seqRun);
                worker.execute();
            }
        } else {
            GetToolsWorker worker = new GetToolsWorker(startUp, master, seqRun);
            worker.execute();
        }
        return null;
    }

    /**
     * Methode, die Ausgefuehrt wird, bevor der SwingWorker fertig ist.
     */
    @Override
    protected void done() {
        LOGGER.info("after installTool");
    }
}