package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.gui.actions.DownloadPluginDump;
import de.cebitec.mgx.gui.nodefactory.ProjectStructureNodeFactory;
import java.awt.Image;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectNode extends MGXNodeBase<MGXMasterI> {

    public ProjectNode(MGXMasterI m) {
        this(m, new ProjectStructureNodeFactory(m));
        String name = new StringBuilder(m.getProject()).append(" (").append(m.getRoleName()).append(")").toString();
        super.setDisplayName(name);
    }

    private ProjectNode(MGXMasterI m, ProjectStructureNodeFactory nf) {
        super(Children.create(nf, false), Lookups.fixed(m), m);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/MGX.png");
    }

    @Override
    public Image getIcon(int type) {
        Image image = super.getIcon(type);
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{new DownloadPluginDump()};
    }

    @Override
    public void updateModified() {
        //
    }
}
