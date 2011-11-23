package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.gui.nodes.ServerNode;
import de.cebitec.mgx.restgpms.GPMS;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ServerNodeFactory extends ChildFactory<GPMSClientI> {

    private GPMS gpms;
    private Lookup lookup;

    public ServerNodeFactory(GPMS gpms, Lookup lookup) {
        this.gpms = gpms;
        this.lookup = lookup;
    }

    @Override
    protected boolean createKeys(List<GPMSClientI> toPopulate) {
        toPopulate.add(gpms);
        return true;
    }

    @Override
    protected Node createNodeForKey(GPMSClientI key) {
        Node n = new ServerNode(Children.create(new ProjectNodeFactory(gpms, lookup), true), Lookups.singleton(key));
        n.setDisplayName(gpms.getServerName());
        return n;
    }
}
