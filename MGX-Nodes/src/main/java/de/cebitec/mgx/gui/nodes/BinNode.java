package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.gui.nodefactory.ContigNodeFactory;
import javax.swing.Action;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class BinNode extends MGXNodeBase<BinI> {

    public BinNode(BinI a) {
        this(a, new ContigNodeFactory(a));
    }

    private BinNode(BinI a, ContigNodeFactory snf) {
        super(Children.create(snf, true), Lookups.fixed(a.getMaster(), a), a);
        super.setDisplayName(a.getName());
        //super.setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        super.setShortDescription(getToolTipText(a));
    }

    private String getToolTipText(BinI h) {
        return new StringBuilder("<html>").append("<b>Assembly: </b>")
                .append(escapeHtml4(h.getName()))
                .append("<br><hr><br>")
                .append("</html>").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        //setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(getContent()));
    }
}
