package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.nodeactions.EditDNAExtract;
import de.cebitec.mgx.gui.nodeactions.DeleteDNAExtract;
import de.cebitec.mgx.gui.nodeactions.AddSeqRun;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.gui.nodefactory.SeqRunNodeFactory;
import javax.swing.Action;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;


/**
 *
 * @author sj
 */
public class DNAExtractNode extends MGXNodeBase<DNAExtractI> {

    public DNAExtractNode(DNAExtractI d) {
        this(d, new SeqRunNodeFactory(d));
    }

    private DNAExtractNode(DNAExtractI d, SeqRunNodeFactory snf) {
        super(Children.create(snf, true), Lookups.fixed(d.getMaster(), d), d);
        super.setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/DNAExtract.png");
        super.setShortDescription(getToolTipText(d));
        super.setDisplayName(d.getName());
    }

    private String getToolTipText(DNAExtractI d) {
        return new StringBuilder("<html><b>DNA extract: </b>")
                .append(escapeHtml4(d.getName()))
                .append("<br><hr><br>")
                .append("Type: ").append(escapeHtml4(d.getMethod())).append("<br>")
                .append("Protocol: ").append(d.getProtocol() != null ? escapeHtml4(d.getProtocol()) : "")
                .append("</html>").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditDNAExtract(), new DeleteDNAExtract(), new AddSeqRun()};
    }

    @Override
    public void updateModified() {
        super.setShortDescription(getToolTipText(getContent()));
        super.setDisplayName(getContent().getName());
    }
}
