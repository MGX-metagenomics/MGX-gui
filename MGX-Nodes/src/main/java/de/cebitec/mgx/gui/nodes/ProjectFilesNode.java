package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.nodeactions.CreateDirectory;
import de.cebitec.mgx.gui.nodeactions.UploadFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectFilesNode extends MGXNodeBase<MGXFileI> {

    private final FileNodeFactory nf;

    public ProjectFilesNode(MGXMasterI master) {
        this(MGXFileI.getRoot(master));
    }

    private ProjectFilesNode(MGXFileI root) {
        this(new FileNodeFactory(root), root);
        setDisplayName("Project Files");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectFiles.png");
    }

    private ProjectFilesNode(FileNodeFactory fnf, MGXFileI root) {
        super(Children.create(fnf, true), Lookups.fixed(root.getMaster(), root), root);
        nf = fnf;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean ctx) {
        return new Action[]{new CreateDirectory(), new UploadFile(nf)};
    }

    @Override
    public void updateModified() {
        //
    }
}
