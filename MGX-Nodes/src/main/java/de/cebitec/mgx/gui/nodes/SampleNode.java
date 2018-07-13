package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.nodeactions.AddExtract;
import de.cebitec.mgx.gui.nodeactions.EditSample;
import de.cebitec.mgx.gui.nodeactions.DeleteSample;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.nodefactory.DNAExtractNodeFactory;
import java.text.DateFormat;
import javax.swing.Action;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;


/**
 *
 * @author sj
 */
public class SampleNode extends MGXNodeBase<SampleI> {

    public SampleNode(SampleI s) {
        this(s, new DNAExtractNodeFactory(s));
    }

    private SampleNode(SampleI s, DNAExtractNodeFactory snf) {
        super(Children.create(snf, true), Lookups.fixed(s.getMaster(), s), s);
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Sample.png");
        super.setShortDescription(getToolTipText(s));
        super.setDisplayName(s.getMaterial());
    }

    private String getToolTipText(SampleI s) {
        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(s.getCollectionDate());
        return new StringBuilder("<html><b>Sample: </b>").append(escapeHtml4(s.getMaterial()))
                .append("<br><hr><br>")
                .append("Collection date: ").append(date).append("<br>")
                .append("</html>").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditSample(), new DeleteSample(), new AddExtract()};
    }

    @Override
    public void updateModified() {
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Sample.png");
        setShortDescription(getToolTipText(getContent()));
        setDisplayName(getContent().getMaterial());
    }
}
