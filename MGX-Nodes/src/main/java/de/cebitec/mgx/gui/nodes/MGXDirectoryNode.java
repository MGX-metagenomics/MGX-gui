package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.nodeactions.CreateDirectory;
import de.cebitec.mgx.gui.actions.DeleteFileOrDirectory;
import de.cebitec.mgx.gui.nodeactions.UploadFile;
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

    public MGXDirectoryNode(MGXFileI f) {
        this(f, new FileNodeFactory(f));
    }

    private MGXDirectoryNode(MGXFileI f, FileNodeFactory fnf) {
        super(Children.create(fnf, true), Lookups.fixed(f.getMaster(), f), f);
        super.setDisplayName(f.getName());
        super.setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
        super.setShortDescription(f.getName());
        this.nf = fnf;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new CreateDirectory(), new DeleteFileOrDirectory(), new UploadFile(nf)};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getContent().getName());
    }
}
