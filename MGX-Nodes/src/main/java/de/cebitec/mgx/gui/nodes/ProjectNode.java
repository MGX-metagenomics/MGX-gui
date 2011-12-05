package de.cebitec.mgx.gui.nodes;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.nodefactory.ProjectStructureNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectNode extends MGXNodeBase {

//    public ProjectNode(MGXMaster m) {
//        super(children, lookup);
//        master = m;
//    }
    private ProjectStructureNodeFactory nf = null;

    public ProjectNode(MGXMaster m, MembershipI mbr) {
        this(m, new ProjectStructureNodeFactory(m));
        master = m;
        String name = new StringBuilder(mbr.getProject().getName())
                .append(" (")
                .append(mbr.getRole().getName())
                .append(")")
                .toString();
        setDisplayName(name);
    }

    private ProjectNode(MGXMaster m, ProjectStructureNodeFactory nf) {
        super(Children.create(nf, false), Lookups.singleton(m));
        this.nf = nf;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
    }
}
