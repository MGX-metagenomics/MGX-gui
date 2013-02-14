package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import java.util.List;
import javax.swing.SwingWorker;

/**
 *
 * @author sjaenick
 */
public class ToolRetriever extends SwingWorker<Pair<List<Tool>, List<Tool>>, Void> {

    private final MGXMaster master;

    public ToolRetriever(MGXMaster master) {
        this.master = master;
    }

    @Override
    protected Pair<List<Tool>, List<Tool>> doInBackground() throws Exception {
        List<Tool> projectTools = master.Tool().fetchall();
        List<Tool> globalTools = master.Tool().listGlobalTools();
        return new Pair<>(globalTools, projectTools);
    }
}
