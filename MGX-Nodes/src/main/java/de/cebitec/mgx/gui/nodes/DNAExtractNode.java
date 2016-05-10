package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.nodeactions.EditDNAExtract;
import de.cebitec.mgx.gui.nodeactions.DeleteDNAExtract;
import de.cebitec.mgx.gui.nodeactions.AddSeqRun;
import de.cebitec.mgx.api.model.DNAExtractI;
import de.cebitec.mgx.gui.nodefactory.SeqRunNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class DNAExtractNode extends MGXNodeBase<DNAExtractI> {

    private final SeqRunNodeFactory snf;

    public DNAExtractNode(DNAExtractI d) {
        this(d, new SeqRunNodeFactory(d));
    }

    private DNAExtractNode(DNAExtractI d, SeqRunNodeFactory snf) {
        super(d.getMaster(), Children.create(snf, true), Lookups.fixed(d.getMaster(), d), d);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/DNAExtract.png");
        setShortDescription(getToolTipText(d));
        setDisplayName(d.getName());
        this.snf = snf;
    }

    private String getToolTipText(DNAExtractI d) {
        return new StringBuilder("<html><b>DNA extract: </b>")
                .append(d.getName())
                .append("<br><hr><br>")
                .append("type: ").append(d.getMethod()).append("<br>")
                .append("protocol: ").append(d.getProtocol() != null ? d.getProtocol() : "")
                .append("</html>").toString();
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditDNAExtract(), new DeleteDNAExtract(), new AddSeqRun(snf)};
    }

    @Override
    public void updateModified() {
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/DNAExtract.png");
        setShortDescription(getToolTipText(getContent()));
        setDisplayName(getContent().getName());
    }


}
