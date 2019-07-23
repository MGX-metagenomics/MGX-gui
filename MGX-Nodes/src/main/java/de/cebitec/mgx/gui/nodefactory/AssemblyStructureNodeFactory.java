package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.gui.nodes.AssembledBinsNode;
import de.cebitec.mgx.gui.nodes.AssembledSeqRunsNode;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author sj
 */
public class AssemblyStructureNodeFactory extends ChildFactory<Node> {

    private final AssembledSeqRunsNode runs;
    private final AssembledBinsNode bins;
    private final MGXMasterI master;

    public AssemblyStructureNodeFactory(MGXMasterI master, AssemblyI assembly) {
        this.master = master;
        // all these nodes have to provide an MGXMaster instance via lookup, 
        // since creation of the "top-level" data objects require a master
        // to access the server
        //
        runs = new AssembledSeqRunsNode(master, assembly);
        bins = new AssembledBinsNode(master, assembly);
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        if (!master.isDeleted()) {
            toPopulate.add(runs);
            toPopulate.add(bins);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Node key) {
        return key;
    }
}
