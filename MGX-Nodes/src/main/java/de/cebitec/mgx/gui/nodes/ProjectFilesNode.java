package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.nodeactions.CreateDirectory;
import de.cebitec.mgx.gui.nodeactions.UploadFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import java.awt.Image;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectFilesNode extends AbstractNode { //MGXNodeBase<MGXFileI> {

//    private final FileNodeFactory nf;
    public ProjectFilesNode(MGXMasterI master) {
        this(MGXFileI.getRoot(master));
    }

    private ProjectFilesNode(MGXFileI root) {
        this(new FileNodeFactory(root), root);
        super.setDisplayName("Project Files");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/files.svg");
    }

    private ProjectFilesNode(FileNodeFactory fnf, MGXFileI root) {
        super(Children.create(fnf, true), Lookups.fixed(root.getMaster(), root));
//        nf = fnf;
    }

    @Override
    public Image getIcon(int type) {
        Image image = super.getIcon(type);
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean ctx) {
        return new Action[]{new CreateDirectory(), new UploadFile()};
    }

//    @Override
//    public void updateModified() {
//        //
//    }
}
