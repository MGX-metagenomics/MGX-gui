/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes.SeqRunNode.ExecuteAnalysisWorker;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import de.cebitec.mgx.gui.wizard.configurations.progressScreen.ProgressBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Der LocalToolWorker laedt ein lokal vorliegendes Tool in das Projekt hoch.
 *
 * @author pbelmann
 */
public class LocalToolWorker extends SwingWorker implements ActionListener, WindowListener {

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
        progress = new ProgressBar("Installing tool...",
                200, 140, this, this, true);
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

        progress.setButton("The tool is installed and available in "
                + "your project tools view.");

//        ProgressBar bar = new ProgressBar();
//        ShowParameterWorker showToolsWorker =
//                new ShowParameterWorker(tool, startUp, master, seqRun);
//        showToolsWorker.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        GetToolsWorker worker = new GetToolsWorker(startUp, master, seqRun);
        worker.execute();
        progress.dispose();
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {

        GetToolsWorker worker = new GetToolsWorker(startUp, master, seqRun);
        worker.execute();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}