package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import java.util.Iterator;
import javax.swing.SwingWorker;

/**
 *
 * @author sjaenick
 */
public class ToolRetriever extends SwingWorker<Pair<Iterator<Tool>, Iterator<Tool>>, Void> {

    private final MGXMaster master;

    public ToolRetriever(MGXMaster master) {
        this.master = master;
    }

    @Override
    protected Pair<Iterator<Tool>, Iterator<Tool>> doInBackground() throws Exception {
        Iterator<Tool> projectTools = master.Tool().fetchall();
        Iterator<Tool> globalTools = master.Tool().listGlobalTools();
        return new Pair<>(globalTools, projectTools);
    }
}
