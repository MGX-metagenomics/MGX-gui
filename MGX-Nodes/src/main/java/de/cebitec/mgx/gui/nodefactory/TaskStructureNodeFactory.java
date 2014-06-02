package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.nodes.ProjectJobsNode;
import de.cebitec.mgx.gui.nodes.ProjectToolsNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author sj
 */
public class TaskStructureNodeFactory extends ChildFactory<String>  {
        private Map<String, Node> structure;

    public TaskStructureNodeFactory(MGXMasterI master) {
        
        // all these nodes have to provide an MGXMaster instance via lookup, 
        // since creation of further server objects require a master
        // to access the server
        //
        ProjectToolsNode tools = new ProjectToolsNode(master);
        ProjectJobsNode jobs = new ProjectJobsNode(master);

        structure = new HashMap<String, Node>();
        structure.put("local_tools", tools);
        structure.put("jobs", jobs);
    }

    @Override
    protected boolean createKeys(List<String> toPopulate) {
        toPopulate.addAll(structure.keySet());
        return true;
    }
    
    @Override
        protected Node createNodeForKey(String key) {
        return structure.get(key);
    }
}
