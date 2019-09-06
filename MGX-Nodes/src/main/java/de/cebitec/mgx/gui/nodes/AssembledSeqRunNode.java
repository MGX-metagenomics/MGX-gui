package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import javax.swing.Action;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class AssembledSeqRunNode extends MGXNodeBase<AssembledSeqRunI> {

    public AssembledSeqRunNode(AssembledSeqRunI data) {
        this(data, Children.LEAF);
    }

    public AssembledSeqRunNode(AssembledSeqRunI s, Children children) {
        super(children, Lookups.fixed(s.getMaster(), s), s);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/SeqRun.png");
        super.setShortDescription(getToolTipText(s));
        super.setDisplayName(s.getName());
    }

    private String getToolTipText(AssembledSeqRunI run) {
        return new StringBuilder("<html><b>Sequencing run: </b>").append(escapeHtml4(run.getName()))
                .append("</html>").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getToolTipText(getContent()));
    }
}
