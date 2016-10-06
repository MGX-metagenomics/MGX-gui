package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.nodefactory.TaskStructureNodeFactory;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectAnalysisTasksNode extends MGXNodeBase<MGXMasterI> {

    public ProjectAnalysisTasksNode(MGXMasterI m) {
        this(m, new TaskStructureNodeFactory(m));
    }

    private ProjectAnalysisTasksNode(MGXMasterI m, TaskStructureNodeFactory nf) {
        super(Children.create(nf, false), Lookups.fixed(m), m);
        super.setDisplayName("Data Analysis");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/AnalysisTasks.png");
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public void updateModified() {
        //
    }
}
