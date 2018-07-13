package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.nodeactions.CreateDirectory;
import de.cebitec.mgx.gui.nodeactions.UploadFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import javax.swing.Action;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class MGXDirectoryNode extends MGXNodeBase<MGXFileI> {

    public MGXDirectoryNode(MGXFileI f) {
        this(f, new FileNodeFactory(f));
    }

    private MGXDirectoryNode(MGXFileI f, FileNodeFactory fnf) {
        super(Children.create(fnf, true), Lookups.fixed(f.getMaster(), f), f);
        super.setDisplayName(f.getName());
        super.setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
        super.setShortDescription(escapeHtml4(f.getName()));
    }

    @Override
    public Action[] getActions(boolean context) {
        Action delAction = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-DeleteFileOrDirectory.instance", Action.class);
        return new Action[]{new CreateDirectory(), delAction, new UploadFile()};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getContent().getName());
    }
}
