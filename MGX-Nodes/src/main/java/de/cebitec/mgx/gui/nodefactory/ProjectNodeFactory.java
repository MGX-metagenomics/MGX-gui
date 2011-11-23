package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.gui.nodes.ProjectNode;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public class ProjectNodeFactory extends ChildFactory<MembershipI> {

    private GPMSClientI gpms;
    private Lookup lookup;

    public ProjectNodeFactory(GPMSClientI gpms, Lookup lookup) {
        this.gpms = gpms;
        this.lookup = lookup;
    }

    @Override
    protected boolean createKeys(List<MembershipI> list) {
        for (MembershipI m : gpms.getMemberships()) {
            if ("MGX".equals(m.getProject().getProjectClass().getName())) {
                list.add(m);
            }
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(MembershipI m) {
        ProjectNode node = new ProjectNode(Children.create(new ProjectStructureNodeFactory(gpms, m, lookup), false), lookup);
        String name = new StringBuilder(m.getProject().getName()).append(" (").append(m.getRole().getName()).append(")").toString();
        node.setDisplayName(name);
        return node;
    }
}
