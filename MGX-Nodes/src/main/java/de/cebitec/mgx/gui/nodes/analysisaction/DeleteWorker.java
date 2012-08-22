
package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *Worker zum Entfernen von Tools
 * @author pbelmann
 */
public class DeleteWorker extends SwingWorker<Void, Void> implements ActionListener {

    /**
     * Masterobjekt, um Verbindung mit dem Server aufzunehmen.
     */
    private MGXMaster master;
    /**
     * Tool welches entfernt werden soll.
     */
    private Tool tool;
    /**
     * Sequenzierlauf.
     */
    private SeqRun seqRun;

    /**
     * Konstruktor zum entfernen von Tools.
     * @param lMaster Masterobjekt
     * @param tool Tool, welches entfernt werden soll.
     * @param lSeqRun Sequenzierlauf
     */
    public DeleteWorker(MGXMaster lMaster, Tool tool, SeqRun lSeqRun) {
        master = lMaster;
        this.tool = tool;
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
        GetToolsWorker worker = new GetToolsWorker(master, seqRun);
        worker.execute();
    }
}
