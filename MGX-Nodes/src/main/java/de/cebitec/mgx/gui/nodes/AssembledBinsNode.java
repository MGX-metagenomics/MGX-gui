package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.gui.nodefactory.BinNodeFactory;
import javax.swing.Action;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class AssembledBinsNode extends AbstractNode { //MGXNodeBase<MGXMasterI> {

    public AssembledBinsNode(MGXMasterI m, AssemblyI asm) {
        this(m, asm, new BinNodeFactory(asm));
    }

    private AssembledBinsNode(MGXMasterI m, AssemblyI asm, BinNodeFactory bnf) {
        super(Children.create(bnf, true), Lookups.fixed(m, asm));
        super.setDisplayName("Bins");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectData.png");
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean context) {
        Action binTable = FileUtil.getConfigObject("Actions/File/de-cebitec-mgx-gui-bintable-BinTableAction.instance", Action.class);
        return new Action[]{binTable};
    }

}
