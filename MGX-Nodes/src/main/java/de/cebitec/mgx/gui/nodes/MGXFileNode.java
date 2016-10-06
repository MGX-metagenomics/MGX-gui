package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.actions.DeleteFileOrDirectory;
import de.cebitec.mgx.gui.actions.DownloadFile;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class MGXFileNode extends MGXNodeBase<MGXFileI> {

    public MGXFileNode(MGXFileI f) {
        super(Children.LEAF, Lookups.fixed(f.getMaster(), f), f);
        super.setDisplayName(f.getName());
        super.setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/File.png");
        super.setShortDescription(f.getName() + " (" + f.getSize() + " bytes)");
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        if (getContent().isDirectory()) {
            return new Action[]{new DeleteFileOrDirectory()};
        } else {
            return new Action[]{new DownloadFile(), new DeleteFileOrDirectory()};
        }
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getContent().getName() + " (" + getContent().getSize() + " bytes)");
    }
}
