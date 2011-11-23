package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.client.MGXMaster;
import de.cebitec.mgx.gui.nodes.ProjectAnalysisTasksNode;
import de.cebitec.mgx.gui.nodes.ProjectDataNode;
import de.cebitec.mgx.gui.nodes.ProjectFilesNode;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author sj
 */
public class ProjectStructureNodeFactory extends ChildFactory<Node> {

    private List<Node> project_structure;

    public ProjectStructureNodeFactory(MGXMaster master) {
        ProjectDataNode data = new ProjectDataNode(Children.create(new HabitatNodeFactory(master), true));
        ProjectFilesNode files = new ProjectFilesNode(Children.LEAF);  // FIXME
        ProjectAnalysisTasksNode tasks = new ProjectAnalysisTasksNode(Children.LEAF); // FIXME

        project_structure = new ArrayList<Node>();
        project_structure.add(data);
        project_structure.add(files);
        project_structure.add(tasks);
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        toPopulate.addAll(project_structure);
        return true;
    }

    @Override
    protected Node createNodeForKey(Node key) {
        return key;
    }
}
