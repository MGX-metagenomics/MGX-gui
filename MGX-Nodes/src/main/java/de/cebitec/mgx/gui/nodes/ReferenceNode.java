package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.DeleteReference;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.actions.OpenMappingByReference;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ReferenceNode extends MGXNodeBase<MGXReferenceI> {

    public ReferenceNode(MGXReferenceI ref) {
        super(Children.LEAF, Lookups.fixed(ref.getMaster(), ref), ref);
        setDisplayName(ref.getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(ref));
    }

    private String getToolTipText(MGXReferenceI ref) {
        return new StringBuilder("<html>").append("<b>Reference: </b>")
                .append(ref.getName())
                .append("<br><hr><br>")
                .append(ref.getLength()).append(" bp<br>")
                .append("</html>").toString();
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new OpenMappingByReference(), new DeleteReference()};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(getContent()));
    }

}
