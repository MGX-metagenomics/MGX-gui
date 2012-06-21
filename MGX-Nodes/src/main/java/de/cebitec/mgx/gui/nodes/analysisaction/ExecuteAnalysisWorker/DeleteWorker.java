/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes.analysisaction.ExecuteAnalysisWorker;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import de.cebitec.mgx.gui.wizard.configurations.progressscreen.ProgressBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author pbelmann
 */
public class DeleteWorker extends SwingWorker implements ActionListener, WindowListener {

    private final static Logger LOGGER =
            Logger.getLogger(DeleteWorker.class.getName());
    private MGXMaster master;
    private long id;
    private WizardController startUp;
    private SeqRun seqRun;
    private ProgressBar bar;

    public DeleteWorker(WizardController lStartUp, MGXMaster lMaster, long lId, SeqRun lSeqRun) {
        master = lMaster;
        id = lId;
        startUp = lStartUp;
        seqRun = lSeqRun;
    }

    @Override
    protected Object doInBackground() {
        LOGGER.info("Delete start");
        bar = new ProgressBar("Delete tool...", 300, 140);
        master.Tool().delete(id);
        bar.setButton("Tool is removed");


        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        bar.dispose();
        GetToolsWorker worker = new GetToolsWorker(startUp, master, seqRun);
        worker.execute();

    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
//        GetToolsWorker worker = new GetToolsWorker(startUp, master, seqRun);
//        worker.execute();
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
