package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.CreateDirectory;
import de.cebitec.mgx.gui.actions.UploadFile;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectFilesNode extends MGXNodeBase<MGXFile> {

    private FileNodeFactory nf;

    public ProjectFilesNode(final MGXMaster m, final MGXFile root) {
        this(new FileNodeFactory(m, root), m, root);
        master = m;
        setDisplayName("Project Files");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectFiles.png");
    }

    private ProjectFilesNode(FileNodeFactory fnf, MGXMaster m, MGXFile root) {
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
        return new Action[]{new CreateDirectory(nf), new UploadFile(nf)}; //, new DeleteDirEntry()};
    }

    @Override
    public void updateModified() {
        //
    }
}
