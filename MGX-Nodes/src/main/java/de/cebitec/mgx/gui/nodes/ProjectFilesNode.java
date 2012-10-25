package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.nodefactory.DirEntryNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectFilesNode extends MGXNodeBase<MGXMaster> {

    public ProjectFilesNode(MGXMaster m) {
        super(Children.create(new DirEntryNodeFactory(m), true), Lookups.singleton(m), m);
        master = m;
        setDisplayName("Project Files");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectFiles.png");
    }

    @Override
    public boolean canDestroy() {
        return false;
    }
        
    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
    }

    @Override
    public void updateModified() {
        //
    }
}
