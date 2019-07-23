package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGX2MasterI;
import de.cebitec.mgx.gui.nodefactory.AssemblyNodeFactory;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectAssembliesNode extends AbstractNode {

    public ProjectAssembliesNode(MGX2MasterI master) {
        this(master, new AssemblyNodeFactory(master));
    }

    private ProjectAssembliesNode(MGX2MasterI m, AssemblyNodeFactory anf) {
        super(Children.create(anf, true), Lookups.fixed(m));
        super.setDisplayName("Assemblies");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectFiles.png");
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean ctx) {
        return new Action[]{};
    }

}
