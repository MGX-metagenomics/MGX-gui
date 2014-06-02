package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.ToolType;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.ToolI;
import java.util.Collection;
import javax.swing.SwingWorker;

/**
 *
 * @author sjaenick
 */
public class ParameterRetriever extends SwingWorker<Collection<JobParameterI>, Void> {

    private final MGXMasterI master;
    private final ToolI tool;
    private final ToolType toolType;

    public ParameterRetriever(MGXMasterI master, ToolI tool, ToolType toolType) {
        this.master = master;
        this.tool = tool;
        this.toolType = toolType;
    }
    
    @Override
    protected Collection<JobParameterI> doInBackground() throws Exception {
        switch (toolType) {
            case GLOBAL:
                return master.Tool().getAvailableParameters(tool.getId(), true);
            case PROJECT:
                return master.Tool().getAvailableParameters(tool.getId(), false);
            case USER_PROVIDED:
                return master.Tool().getAvailableParameters(tool);
            default:
                assert false;
                return null;
        }
    }
}
