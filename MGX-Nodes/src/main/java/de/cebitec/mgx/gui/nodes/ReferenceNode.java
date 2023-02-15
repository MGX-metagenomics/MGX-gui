package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.DeleteReference;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.actions.OpenMappingByReference;
import java.awt.Image;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.Action;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ReferenceNode extends MGXNodeBase<MGXReferenceI> {

    public ReferenceNode(MGXReferenceI ref) {
        super(Children.LEAF, Lookups.fixed(ref.getMaster(), ref), ref);
        super.setDisplayName(ref.getName());
        super.setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Genome.png");
        super.setShortDescription(getToolTipText(ref));
    }

    @Override
    public Image getIcon(int type) {
        Image image = super.getIcon(type);
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
    }

    private String getToolTipText(MGXReferenceI ref) {
        return new StringBuilder("<html>").append("<b>Reference: </b>")
                .append(escapeHtml4(ref.getName()))
                .append("<br><hr><br>")
                .append(NumberFormat.getInstance(Locale.US).format(ref.getLength())).append(" bp<br>")
                .append("</html>").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new OpenMappingByReference(), new DeleteReference()};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Genome.png");
        setShortDescription(getToolTipText(getContent()));
    }

}
