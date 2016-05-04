package de.cebitec.gpms.nodefactory;

import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.gpms.node.ServerNode;
import de.cebitec.gpms.server.ServerFactory;
import de.cebitec.mgx.pevents.ParallelPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author sj
 */
public class ServerNodeFactory extends ChildFactory<GPMSClientI> implements PropertyChangeListener {

    private final ParallelPropertyChangeSupport pcs = new ParallelPropertyChangeSupport(this);

    public ServerNodeFactory() {
        /*
         *  listen on server factory to get notified of servers being added
         *  or removed
         */
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
        Collections.sort(servers, new Comparator<GPMSClientI>() {
            @Override
            public int compare(GPMSClientI o1, GPMSClientI o2) {
                return o1.getServerName().compareTo(o2.getServerName());
            }

        });
        return true;
    }

    @Override
    protected Node createNodeForKey(GPMSClientI key) {
        ServerNode ret = new ServerNode(key);
        ret.addPropertyChangeListener(this);
        return ret;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        pcs.firePropertyChange(evt);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
}
