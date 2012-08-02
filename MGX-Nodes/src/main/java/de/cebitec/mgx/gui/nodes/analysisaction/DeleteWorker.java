
package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * @author pbelmann
 */
public class DeleteWorker extends SwingWorker<Void, Void> implements ActionListener {

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
    protected Void doInBackground() {
        master.Tool().delete(tool);
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        super.done();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GetToolsWorker worker = new GetToolsWorker(startUp, master, seqRun);
        worker.execute();
    }
}
