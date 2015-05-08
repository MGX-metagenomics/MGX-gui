package de.cebitec.mgx.gui.nodes;

import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.gpms.rest.RESTUserI;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public class ServerNode extends AbstractNode {

    private final GPMSClientI gpmsclient;

    public ServerNode(GPMSClientI client, Children children, Lookup lookup) {
        super(children, lookup);
        this.gpmsclient = client;
        RESTUserI user = gpmsclient.getUser();
        if (user != null) {
            setShortDescription(client.getServerName() + " (Connected as " + gpmsclient.getUser().getLogin() + ")");
        }
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Server.png");
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
    }
}
