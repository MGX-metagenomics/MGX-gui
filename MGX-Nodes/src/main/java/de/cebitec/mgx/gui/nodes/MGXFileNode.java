package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.actions.DownloadFile;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.Action;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.openide.filesystems.FileUtil;
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
        super.setShortDescription(escapeHtml4(f.getName()) + " (" + NumberFormat.getInstance(Locale.US).format(f.getSize()) + " bytes)");
    }

    @Override
    public Action[] getActions(boolean context) {
        Action delAction = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-DeleteFileOrDirectory.instance", Action.class);
        if (getContent().isDirectory()) {
            return new Action[]{delAction};
        } else {
            return new Action[]{new DownloadFile(), delAction};
        }
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getContent().getName() + " (" + getContent().getSize() + " bytes)");
    }
}
