package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.nodes.ProjectJobsNode;
import de.cebitec.mgx.gui.nodes.ProjectToolsNode;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author sj
 */
public class TaskStructureNodeFactory extends ChildFactory<Node> {

    private final ProjectToolsNode tools;
    private final ProjectJobsNode jobs;

    public TaskStructureNodeFactory(MGXMasterI master) {

        // all these nodes have to provide an MGXMaster instance via lookup, 
        // since creation of further server objects require a master
        // to access the server
        //
        tools = new ProjectToolsNode(master);
        jobs = new ProjectJobsNode(master);
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        toPopulate.add(tools);
        toPopulate.add(jobs);
        return true;
    }

    @Override
    protected Node createNodeForKey(Node key) {
        return key;
    }
}
