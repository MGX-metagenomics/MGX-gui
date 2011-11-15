package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.gui.nodes.ServerNode;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author sj
 */
public class ServerNodeFactory extends ChildFactory<GPMSClientI> {

    private GPMSClientI gpms;

    public ServerNodeFactory(GPMSClientI gpms) {
        this.gpms = gpms;
    }

    @Override
    protected boolean createKeys(List<GPMSClientI> toPopulate) {
        toPopulate.add(gpms);
        return true;
    }

    @Override
    protected Node createNodeForKey(GPMSClientI key) {
        return new ServerNode(Children.create(new ProjectNodeFactory(gpms), true));
    }
}
