package de.cebitec.mgx.gui.nodes;

import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public class ServerNode extends AbstractNode {

    public ServerNode(Children children, Lookup lookup) {
        super(children, lookup);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Server.png");
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
    }
}
