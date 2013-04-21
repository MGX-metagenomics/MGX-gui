package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import java.util.Iterator;
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
     * Das Tool, welches vom Benutzer ausgesucht wurde.
     */
    private Tool tool;
    /**
     * Die verschiedenen Tooltypen.
     */
    private ToolType toolType;
    /**
     * Das Masterobjekt um eine Verbindung zum Server aufzunehmen.
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

    public GetToolsWorker(final MGXMaster lMaster, final SeqRun lSeqrun) {
        master = lMaster;
        seqRun = lSeqrun;
        isDelete = false;
    }

    /**
     * Methode wird im Hintergrund bearbeitet.
     *
     * @return null
     */
    @Override
    final protected Void doInBackground() {
        WizardController startUp = new WizardController();

        try {
            Iterator<Tool> globalTools = master.Tool().listGlobalTools();
            Iterator<Tool> projectTools = master.Tool().fetchall();
            tool = startUp.startToolViewStartUp(globalTools, projectTools);
        } catch (MGXServerException ex) {
            Exceptions.printStackTrace(ex);
        }

        isDelete = startUp.isToolDelete();
        toolType = startUp.getLastToolType();
        return null;
    }

    @Override
    final protected void done() {
        if (tool != null) {
            if (isDelete) {
                DeleteWorker deleteWorker =
                        new DeleteWorker(master, tool, seqRun);
                deleteWorker.execute();
            } else {
                ShowParameterWorker worker =
                        new ShowParameterWorker(tool, master, seqRun, toolType);
                worker.execute();
            }
        }
    }
}
