package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.access.MGXMaster;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public class ProjectNode extends AbstractNode {

    private MGXMaster master;

    public ProjectNode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    public void setMaster(MGXMaster m) {
        master = m;
    }
    
    public MGXMaster getMaster() {
        return master;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }
    
    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
    }
}
