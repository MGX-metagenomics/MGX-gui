package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectToolsNode extends MGXNodeBase<MGXMasterI> {

    public ProjectToolsNode(MGXMasterI m) {
        super(m, Children.LEAF, Lookups.singleton(m), m);
        setDisplayName("Tools");
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public void updateModified() {
        //
    }
}
