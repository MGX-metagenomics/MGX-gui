
package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 *
 * @author pbelmann
 */
public class DeleteWorker extends SwingWorker implements ActionListener {

    private final static Logger LOGGER =
            Logger.getLogger(DeleteWorker.class.getName());
    private MGXMaster master;
    private Tool tool;
    private WizardController startUp;
    private SeqRun seqRun;

    public DeleteWorker(WizardController lStartUp, MGXMaster lMaster, Tool tool, SeqRun lSeqRun) {
        master = lMaster;
        this.tool = tool;
        startUp = lStartUp;
        seqRun = lSeqRun;
    }

    @Override
    protected Object doInBackground() {
        master.Tool().delete(tool);
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GetToolsWorker worker = new GetToolsWorker(startUp, master, seqRun);
        worker.execute();

    }
}
