package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.nodes.ProjectNode;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

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
        ProjectNode node = new ProjectNode(master, m); 
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
