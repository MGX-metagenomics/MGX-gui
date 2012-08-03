package de.cebitec.mgx.gui.nodes.analysisaction;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.*;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.configurations.action.WizardController;
import de.cebitec.mgx.gui.wizard.configurations.data.impl.Store;
import de.cebitec.mgx.gui.wizard.configurations.data.util.Transform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.util.Exceptions;

/**
 *
 * ShowParameterWorker fuer die Anzeige von Parametern.
 *
 * @author pbelmann
 */
public class ShowParameterWorker extends SwingWorker<Collection<JobParameter>, Void> {

    private Tool tool;
    private WizardController startUp;
    private MGXMaster master;
    private ToolType toolType;
    private SeqRun seqRun;
    private Collection<JobParameter> list = null;

    public ShowParameterWorker(Tool lTool, WizardController startUp, MGXMaster master,
            SeqRun lSeqRun,
            ToolType toolType) {
        this.master = master;
        tool = lTool;
        this.startUp = startUp;
        this.toolType = toolType;
        seqRun = lSeqRun;
    }

    @Override
    protected Collection<JobParameter> doInBackground() {



        switch (toolType) {
            case GLOBAL:
                list = master.Tool().getAvailableParameters(tool.getId(), true);
                break;
            case PROJECT:
                list = master.Tool().getAvailableParameters(tool.getId(), false);
                break;
            case USER_PROVIDED:
                list = master.Tool().getAvailableParameters(tool);
                break;
            default:
                assert false;
        }

        Store store = Transform.getFromJobParameterNodeStore(new ArrayList<JobParameter>(list));
        List<DirEntry> files = master.File().fetchall();

        if (list.size() > 0) {
            startUp.startParameterConfiguration(store, files, tool.getName());
        }
        return list;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }

        JobWorker worker = new JobWorker(toolType, tool, new ArrayList(list), master, seqRun);
        worker.execute();
    }
}
