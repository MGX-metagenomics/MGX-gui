package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.nodes.ProjectAssembliesNode;
import de.cebitec.mgx.gui.nodes.ProjectDataNode;
import de.cebitec.mgx.gui.nodes.ProjectFilesNode;
import de.cebitec.mgx.gui.nodes.ProjectReferencesNode;
import java.util.List;
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
    private final ProjectAssembliesNode asms;
    private final MGXMasterI master;

    public ProjectStructureNodeFactory(MGXMasterI master) {
        this.master = master;
        // all these nodes have to provide an MGXMaster instance via lookup, 
        // since creation of the "top-level" data objects require a master
        // to access the server
        //
        files = new ProjectFilesNode(master);
        refs = new ProjectReferencesNode(master);
        data = new ProjectDataNode(master);
        asms = new ProjectAssembliesNode(master);
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        if (!master.isDeleted()) {
            toPopulate.add(files);
            toPopulate.add(refs);
            toPopulate.add(data);
            toPopulate.add(asms);
            // don't sort here
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Node key) {
        return key;
    }
}
