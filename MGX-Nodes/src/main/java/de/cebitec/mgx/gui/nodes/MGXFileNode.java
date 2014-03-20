package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.DeleteFileOrDirectory;
import de.cebitec.mgx.gui.actions.DownloadFile;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class MGXFileNode extends MGXNodeBase<MGXFile, MGXFileNode> {

    public MGXFileNode(MGXFile f, MGXMaster m) {
        super(Children.LEAF, Lookups.fixed(m, f), f);
        master = m;
        setDisplayName(f.getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/File.png");
        setShortDescription(f.getName());
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action getPreferredAction() {
        return null;
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
        setShortDescription(getContent().getName());
    }
}
