package de.cebitec.mgx.gui.nodes;

import de.cebitec.gpms.core.UserI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.gui.nodeactions.DisconnectAction;
import de.cebitec.mgx.gui.nodefactory.ProjectNodeFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ServerNode extends AbstractNode implements PropertyChangeListener {

    private final GPMSClientI gpmsclient;

    public ServerNode(GPMSClientI client) {
        super(Children.create(new ProjectNodeFactory(client), true), Lookups.singleton(client));
        this.gpmsclient = client;
        
        setDisplayName(gpmsclient.getServerName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Server.png");
        
        UserI user = gpmsclient.getUser();
        if (user != null) {
            setShortDescription(gpmsclient.getServerName() + " (Connected as " + gpmsclient.getUser().getLogin() + ")");
        } else {
            setShortDescription(gpmsclient.getServerName() + " (Not connected)");
        }

        gpmsclient.addPropertyChangeListener(this);

    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{new DisconnectAction()};
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(gpmsclient) && GPMSClientI.PROP_LOGGEDIN.equals(evt.getPropertyName())) {
            if (gpmsclient.loggedIn()) {
                setShortDescription(gpmsclient.getServerName() + " (Connected as " + gpmsclient.getUser().getLogin() + ")");
            } else {
                setShortDescription(gpmsclient.getServerName() + " (Not connected)");
            }
        }
    }

    @Override
    public void destroy() throws IOException {
        super.destroy();
        gpmsclient.removePropertyChangeListener(this);
    }

}
