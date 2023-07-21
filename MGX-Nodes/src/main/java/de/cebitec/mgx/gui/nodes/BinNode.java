package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.model.assembly.BinI;
import java.awt.Image;
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
public class BinNode extends MGXNodeBase<BinI> {

    public BinNode(BinI a) {
        super(Children.LEAF, Lookups.fixed(a.getMaster(), a), a);
        super.setDisplayName(a.getName());
        super.setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Bin.png");
        super.setShortDescription(getToolTipText(a));
    }

    @Override
    public Image getIcon(int type) {
        Image image = super.getIcon(type);
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
    }

    private String getToolTipText(BinI h) {
        return new StringBuilder("<html>").append("<b>Bin: </b>")
                .append(escapeHtml4(h.getName()))
                .append("<br><hr><br>")
                .append("Completeness: ")
                .append(h.getCompleteness())
                .append("%<br>Contamination: ")
                .append(h.getContamination())
                .append("%<br>Size: ")
                .append(NumberFormat.getInstance(Locale.US).format(h.getTotalSize()))
                .append(" bp<br>Contigs: ")
                .append(NumberFormat.getInstance(Locale.US).format(h.getNumContigs()))
                .append("<br>N50: ")
                .append(NumberFormat.getInstance(Locale.US).format(h.getN50()))
                .append(" bp<br>CDS: ")
                .append(NumberFormat.getInstance(Locale.US).format(h.getPredictedCDS()))
                .append("<br>Taxonomy: ")
                .append(h.getTaxonomy())
                .append("</html>").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        Action binTable = FileUtil.getConfigObject("Actions/File/de-cebitec-mgx-gui-bintable-BinTableAction.instance", Action.class);
        Action blobogram = FileUtil.getConfigObject("Actions/File/de-cebitec-mgx-gui-blobogram-BlobogramAction.instance", Action.class);
        Action exportGBK = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-ExportBin.instance", Action.class);
        Action exportFAS = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-ExportBinFasta.instance", Action.class);
        Action exportCDSFNA = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-ExportBinCDSFNA.instance", Action.class);
        Action exportFAA = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-ExportBinFAA.instance", Action.class);
        return new Action[]{binTable, blobogram, exportGBK, exportFAS, exportCDSFNA, exportFAA};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        //setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(getContent()));
    }
}
