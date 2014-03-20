package de.cebitec.mgx.gui.nodes;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.mgx.gui.actions.DownloadPluginDump;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.nodefactory.ProjectStructureNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectNode extends MGXNodeBase<MGXMaster, ProjectNode> {

    //private ProjectStructureNodeFactory nf = null;

    public ProjectNode(MGXMaster m, MembershipI mbr) {
        this(m, new ProjectStructureNodeFactory(m));
        master = m;
        String name = new StringBuilder(mbr.getProject().getName()).append(" (").append(mbr.getRole().getName()).append(")").toString();
        setDisplayName(name);
    }

    private ProjectNode(MGXMaster m, ProjectStructureNodeFactory nf) {
        super(Children.create(nf, false), Lookups.fixed(m), m);
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
