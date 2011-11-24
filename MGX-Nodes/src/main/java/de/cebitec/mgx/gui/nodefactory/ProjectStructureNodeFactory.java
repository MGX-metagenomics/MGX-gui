package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.gui.nodes.ProjectAnalysisTasksNode;
import de.cebitec.mgx.gui.nodes.ProjectDataNode;
import de.cebitec.mgx.gui.nodes.ProjectFilesNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectStructureNodeFactory extends ChildFactory<String> {

    private Map<String, Node> project_structure;

    public ProjectStructureNodeFactory(MGXMaster master) {
        
        // all these nodes have to provide an MGXMaster instance via lookup, 
        // since creation of the "top-level" data objects require a master
        // to access the server
        //
        ProjectDataNode data = new ProjectDataNode(Children.create(new HabitatNodeFactory(master), true), Lookups.singleton(master));
        ProjectFilesNode files = new ProjectFilesNode(Children.LEAF, Lookups.singleton(master));  // FIXME
        ProjectAnalysisTasksNode tasks = new ProjectAnalysisTasksNode(Children.LEAF, Lookups.singleton(master)); // FIXME

        project_structure = new HashMap<String, Node>();
        project_structure.put("data", data);
        project_structure.put("files", files);
        project_structure.put("tasks", tasks);
    }

    @Override
    protected boolean createKeys(List<String> toPopulate) {
        for (String key : project_structure.keySet()) {
            toPopulate.add(key);
        }
        return true;
    }
    
    @Override
        protected Node createNodeForKey(String key) {
        return project_structure.get(key);
    }
}
