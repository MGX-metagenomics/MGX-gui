package de.cebitec.mgx.gui.wizard.analysis.workers;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.ToolType;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.JobParameterI;
import java.util.Collection;
import javax.swing.SwingWorker;

/**
 *
 * @author sjaenick
 */
public class ParameterRetriever extends SwingWorker<Collection<JobParameterI>, Void> {

    private final MGXMasterI master;
    private final long toolId;
    private final ToolType toolType;
    private final String toolXml;

    public ParameterRetriever(MGXMasterI master, long toolId, ToolType toolType) {
        this.master = master;
        this.toolId = toolId;
        this.toolType = toolType;
        this.toolXml = null;
    }

    public ParameterRetriever(MGXMasterI master, String toolXml) {
        this.master = master;
        this.toolId = Identifiable.INVALID_IDENTIFIER;
        this.toolType = ToolType.USER_PROVIDED;
        this.toolXml = toolXml;
    }

    @Override
    protected Collection<JobParameterI> doInBackground() throws Exception {
        switch (toolType) {
            case GLOBAL:
                return master.Tool().getAvailableParameters(toolId, true);
            case PROJECT:
                return master.Tool().getAvailableParameters(toolId, false);
            case USER_PROVIDED:
                return master.Tool().getAvailableParameters(toolXml);
            default:
                assert false;
                return null;
        }
    }
}
