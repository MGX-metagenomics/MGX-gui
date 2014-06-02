package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.ToolI;
import java.util.Iterator;
import javax.swing.SwingWorker;

/**
 *
 * @author sjaenick
 */
public class ToolRetriever extends SwingWorker<Pair<Iterator<ToolI>, Iterator<ToolI>>, Void> {

    private final MGXMasterI master;

    public ToolRetriever(MGXMasterI master) {
        this.master = master;
    }

    @Override
    protected Pair<Iterator<ToolI>, Iterator<ToolI>> doInBackground() throws Exception {
        Iterator<ToolI> projectTools = master.Tool().fetchall();
        Iterator<ToolI> globalTools = master.Tool().listGlobalTools();
        return new Pair<>(globalTools, projectTools);
    }
}
