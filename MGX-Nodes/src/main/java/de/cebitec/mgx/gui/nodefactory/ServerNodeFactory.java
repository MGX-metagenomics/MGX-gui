package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.gui.nodes.ServerNode;
import de.cebitec.mgx.gui.server.ServerFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author sj
 */
public class ServerNodeFactory extends ChildFactory<GPMSClientI> {

    public ServerNodeFactory() {
        ServerFactory.getDefault().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getSource() instanceof ServerFactory && evt.getPropertyName().equals(ServerFactory.PROP_CHANGED)) {
                    refresh(true);
                }
            }
        });
    }

    @Override
    protected boolean createKeys(List<GPMSClientI> toPopulate) {
        List<GPMSClientI> servers = ServerFactory.getDefault().getServers();
        for (GPMSClientI gc : servers) {
            toPopulate.add(gc);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(GPMSClientI key) {
        return new ServerNode(key);
    }
}
