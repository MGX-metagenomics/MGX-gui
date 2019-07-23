package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGX2MasterI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.gui.nodefactory.BinNodeFactory;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class AssembledBinsNode extends AbstractNode { //MGXNodeBase<MGXMasterI> {

    public AssembledBinsNode(MGX2MasterI m, AssemblyI asm) {
        this(m, new BinNodeFactory(asm));
    }

    private AssembledBinsNode(MGX2MasterI m, BinNodeFactory bnf) {
        super(Children.create(bnf, true), Lookups.fixed(m));
        super.setDisplayName("Bins");
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
