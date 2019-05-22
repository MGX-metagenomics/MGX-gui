package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.nodeactions.CreateDirectory;
import de.cebitec.mgx.gui.nodeactions.UploadFile;
import de.cebitec.mgx.gui.nodefactory.AssemblyNodeFactory;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectAssembliesNode extends AbstractNode {

    public ProjectAssembliesNode(MGXMasterI master) {
        this(master, new AssemblyNodeFactory(master));
    }

    private ProjectAssembliesNode(MGXMasterI m, AssemblyNodeFactory anf) {
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
