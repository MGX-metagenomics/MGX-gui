package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.core.ProjectI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXMaster;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author sj
 */
public class ProjectNodeFactory extends ChildFactory<MembershipI> {

    private GPMSClientI gpms;

    public ProjectNodeFactory(GPMSClientI gpms) {
        this.gpms = gpms;
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
        AbstractNode project = new AbstractNode(Children.create(new HabitatNodeFactory(new MGXMaster(gpms, m)), true));
        String name = new StringBuilder(m.getProject().getName()).append(" (").append(m.getRole().getName()).append(")").toString();
        project.setDisplayName(name);
        return project;
    }
    
    
}
