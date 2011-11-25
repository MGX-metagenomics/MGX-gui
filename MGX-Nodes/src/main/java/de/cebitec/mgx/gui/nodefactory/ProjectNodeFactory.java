package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.access.MGXMaster;
import de.cebitec.mgx.gui.nodes.ProjectNode;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectNodeFactory extends ChildFactory<MembershipI> implements NodeListener {

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
        MGXDTOMaster dtomaster = new MGXDTOMaster(gpms, m);
        MGXMaster master = new MGXMaster(dtomaster);
        ProjectNode node = new ProjectNode(Children.create(new ProjectStructureNodeFactory(master), false), Lookups.singleton(master));
        String name = new StringBuilder(m.getProject().getName()).append(" (").append(m.getRole().getName()).append(")").toString();
        node.setDisplayName(name);
        node.setMaster(master);
        node.addNodeListener(this);
        return node;
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
       // this.refresh(true);
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
       // this.refresh(true);
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
        //this.refresh(true);
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        this.refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        //this.refresh(true);
    }
}
