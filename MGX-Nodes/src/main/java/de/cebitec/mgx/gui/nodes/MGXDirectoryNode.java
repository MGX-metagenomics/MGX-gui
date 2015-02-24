package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.actions.CreateDirectory;
import de.cebitec.mgx.gui.actions.DeleteFileOrDirectory;
import de.cebitec.mgx.gui.actions.UploadFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class MGXDirectoryNode extends MGXNodeBase<MGXFileI> {

    private FileNodeFactory nf = null;

    public MGXDirectoryNode(MGXFileI f, MGXMasterI m) {
        this(f, m, new FileNodeFactory(m, f));
    }

    private MGXDirectoryNode(MGXFileI f, MGXMasterI m, FileNodeFactory fnf) {
        super(m, Children.create(fnf, true), Lookups.fixed(m, f), f);
        setDisplayName(f.getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
        setShortDescription(f.getName());
        this.nf = fnf;
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
        return new Action[]{new CreateDirectory(nf), new DeleteFileOrDirectory(), new UploadFile(nf)};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getContent().getName());
    }
}
