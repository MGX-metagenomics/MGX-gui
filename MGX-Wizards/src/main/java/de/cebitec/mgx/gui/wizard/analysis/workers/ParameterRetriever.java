package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import java.util.Collection;
import javax.swing.SwingWorker;

/**
 *
 * @author sjaenick
 */
public class ParameterRetriever extends SwingWorker<Collection<JobParameter>, Void> {

    private final MGXMaster master;
    private final Tool tool;
    private final ToolType toolType;

    public ParameterRetriever(MGXMaster master, Tool tool, ToolType toolType) {
        this.master = master;
        this.tool = tool;
        this.toolType = toolType;
    }
    
    @Override
    protected Collection<JobParameter> doInBackground() throws Exception {
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
