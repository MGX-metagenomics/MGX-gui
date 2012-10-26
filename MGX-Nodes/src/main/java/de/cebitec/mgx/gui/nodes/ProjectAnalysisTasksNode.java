package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.nodefactory.TaskStructureNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectAnalysisTasksNode extends MGXNodeBase<MGXMaster> {

    private TaskStructureNodeFactory nf = null;

    public ProjectAnalysisTasksNode(MGXMaster m) {
        this(m, new TaskStructureNodeFactory(m));
        master = m;
    }

    private ProjectAnalysisTasksNode(MGXMaster m, TaskStructureNodeFactory nf) {
        super(Children.create(nf, false), Lookups.fixed(m), m);
        this.nf = nf;
        setDisplayName("Data Analysis");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/AnalysisTasks.png");
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
    }

    @Override
    public void updateModified() {
        //
    }
}
