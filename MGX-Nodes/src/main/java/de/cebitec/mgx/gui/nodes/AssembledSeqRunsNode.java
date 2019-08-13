package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.gui.nodefactory.AssembledSeqrunsNodeFactory;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class AssembledSeqRunsNode extends AbstractNode {

    public AssembledSeqRunsNode(MGXMasterI m, AssemblyI assembly) {
        this(m, new AssembledSeqrunsNodeFactory(assembly));
    }

    private AssembledSeqRunsNode(MGXMasterI m, AssembledSeqrunsNodeFactory anf) {
        super(Children.create(anf, true), Lookups.fixed(m));
        super.setDisplayName("Sequencing runs");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectData.png");
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{};
    }

}
