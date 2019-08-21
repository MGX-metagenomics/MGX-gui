package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.model.assembly.ContigI;
import javax.swing.Action;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ContigNode extends MGXNodeBase<ContigI> {

    public ContigNode(ContigI a) {
        super(Children.LEAF, Lookups.fixed(a.getMaster(), a), a);
        super.setDisplayName("contig" + a.getId());
        //super.setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        super.setShortDescription(getToolTipText(a));
    }

    private String getToolTipText(ContigI h) {
        return new StringBuilder("<html>").append("<b>Contig: </b>")
                .append(h.getName())
                .append("<br><hr><br>")
                .append("Length: ")
                .append(h.getLength())
                .append(" bp<br>")
                .append("GC: ")
                .append(h.getGC())
                .append("%<br>Coverage: ")
                .append(h.getCoverage())
                .append(h.getPredictedCDS())
                .append("</html>").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{};
    }

    @Override
    public void updateModified() {
        setDisplayName("contig" + getContent().getId());
        //setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(getContent()));
    }
}
