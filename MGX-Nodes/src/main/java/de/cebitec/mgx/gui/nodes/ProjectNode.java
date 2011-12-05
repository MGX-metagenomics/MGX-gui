package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import javax.swing.Action;

/**
 *
 * @author sj
 */
public class ProjectNode extends MGXNodeBase {

    public ProjectNode(MGXMaster m) {
        super(children, lookup);
        master = m;
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
