package de.cebitec.mgx.gui.nodes;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.actions.DownloadPluginDump;
import de.cebitec.mgx.gui.nodefactory.ProjectStructureNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectNode extends MGXNodeBase<MGXMasterI> {

    //private ProjectStructureNodeFactory nf = null;

    public ProjectNode(MGXMasterI m, MembershipI mbr) {
        this(m, new ProjectStructureNodeFactory(m));
        String name = new StringBuilder(mbr.getProject().getName()).append(" (").append(mbr.getRole().getName()).append(")").toString();
        setDisplayName(name);
    }

    private ProjectNode(MGXMasterI m, ProjectStructureNodeFactory nf) {
        super(m, Children.create(nf, false), Lookups.fixed(m), m);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/mgx.png");
        //this.nf = nf;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{new DownloadPluginDump()};
    }

    @Override
    public void updateModified() {
        //
    }
}
