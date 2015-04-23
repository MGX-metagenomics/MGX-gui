package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.nodes.ProjectDataNode;
import de.cebitec.mgx.gui.nodes.ProjectFilesNode;
import de.cebitec.mgx.gui.nodes.ProjectReferencesNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author sj
 */
public class ProjectStructureNodeFactory extends ChildFactory<Node> {

    private final ProjectDataNode data;
    private final ProjectFilesNode files;
    private final ProjectReferencesNode refs;

    public ProjectStructureNodeFactory(MGXMasterI master) {

        // all these nodes have to provide an MGXMaster instance via lookup, 
        // since creation of the "top-level" data objects require a master
        // to access the server
        //
        files = new ProjectFilesNode(MGXFileI.getRoot(master));
        refs = new ProjectReferencesNode(master);
        data = new ProjectDataNode(master);
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        toPopulate.add(files);
        toPopulate.add(refs);
        toPopulate.add(data);
        // don't sort here
        return true;
    }

    @Override
    protected Node createNodeForKey(Node key) {
        return key;
    }
}
