package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.actions.CreateDirectory;
import de.cebitec.mgx.gui.actions.UploadFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectFilesNode extends MGXNodeBase<MGXFileI, ProjectFilesNode> {

    private final FileNodeFactory nf;

    public ProjectFilesNode(final MGXMasterI m, final MGXFileI root) {
        this(new FileNodeFactory(m, root), m, root);
        master = m;
        setDisplayName("Project Files");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectFiles.png");
    }

    private ProjectFilesNode(FileNodeFactory fnf, MGXMasterI m, MGXFileI root) {
        super(Children.create(fnf, true), Lookups.fixed(m, root), root);
        nf = fnf;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action getPreferredAction() {
        return null;
    }

    @Override
    public Action[] getActions(boolean ctx) {
        return new Action[]{new CreateDirectory(nf), new UploadFile(nf)};
    }

    @Override
    public void updateModified() {
        //
    }
}
