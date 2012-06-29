/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes.analysisaction;

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
 * Der LocalToolWorker laedt ein lokal vorliegendes Tool in das Projekt hoch.
 *
 * @author pbelmann
 */
public class LocalToolWorker extends SwingWorker {

    private final static Logger LOGGER =
            Logger.getLogger(LocalToolWorker.class.getName());
    /**
     * Tool, welches hochgeladen werden soll.
     */
    private Tool tool;
    /**
     * ID des Tools.
     */
    private long tool_id;
    /**
     * GuiController.
     */
    private WizardController startUp;
    /**
     * Masterobjekt, welches fuer die Verbindung mit dem Server verantwortlich
     * ist.
     */
    private MGXMaster master;
    /**
     * Sequenzierlauf.
     */
    private SeqRun seqRun;
    private ProgressBar progress;

    /**
     * Konstruktor
     *
     * @param lTool lokales Tool.
     * @param startup GuiController
     * @param lMaster Masterobjekt.
     * @param seqRun Sequenzierlauf.
     */
    public LocalToolWorker(Tool lTool, WizardController startup, MGXMaster lMaster,
            SeqRun seqRun) {
        tool = lTool;
        this.seqRun = seqRun;
        startUp = startup;
        master = lMaster;
    }

    /**
     * Methode, welche im Hintergrund arbeitet.
     *
     * @return Objekt.
     */
    @Override
    protected Object doInBackground() {
        LOGGER.info("localTool");
        progress = new ProgressBar("Installing tool.","Waiting for the server",
                300, 140);
        tool_id = master.Tool().create(tool);
        return null;
    }

    /**
     * Methode die beim beenden des Swingworkers aufgerufen wird.
     */
    @Override
    protected void done() {
        LOGGER.info("localToolWorker");
        if (tool != null) {
            LOGGER.info("after localTool create TOOLID: " + tool.getId());
        }

        progress.dispose();
        String[] ok = {"ok"};
        int answer = JOptionPane.showOptionDialog(null, 
                "The tool is installed and available in "
                + "your project tool overview.",
                "Notification",
                JOptionPane.OK_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, ok, "OK");
        if (answer == JOptionPane.OK_OPTION || 
                answer == JOptionPane.CLOSED_OPTION) {
            GetToolsWorker worker = new GetToolsWorker(startUp, master, seqRun);
            worker.execute();
        }
    }
}