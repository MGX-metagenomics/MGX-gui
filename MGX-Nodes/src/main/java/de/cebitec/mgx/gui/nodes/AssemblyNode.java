package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.gui.nodefactory.AssemblyStructureNodeFactory;
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
public class AssemblyNode extends MGXNodeBase<AssemblyI> {

    public AssemblyNode(MGXMasterI m, AssemblyI a) {
        this(a, new AssemblyStructureNodeFactory(m, a));
    }

    private AssemblyNode(AssemblyI a, AssemblyStructureNodeFactory snf) {
        super(Children.create(snf, true), Lookups.fixed(a.getMaster(), a), a);
        super.setDisplayName(a.getName());
        super.setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Assembly.png");
        super.setShortDescription(getToolTipText(a));
    }

    @Override
    public Image getIcon(int type) {
        Image image = super.getIcon(type);
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
    }

    private String getToolTipText(AssemblyI h) {
        return new StringBuilder("<html>").append("<b>Assembly: </b>")
                .append(escapeHtml4(h.getName()))
                .append("<br><hr><br>")
                .append("Assembled reads: ")
                .append(NumberFormat.getInstance(Locale.US).format(h.getReadsAssembled()))
                .append("<br>N50: ")
                .append(NumberFormat.getInstance(Locale.US).format(h.getN50()))
                .append(" bp<br>CDS: ")
                .append(NumberFormat.getInstance(Locale.US).format(h.getNumberCDS()))
                .append("</html>").toString();

    }

    @Override
    public Action[] getActions(boolean context) {
        Action annotate = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-AnnotateAssembly.instance", Action.class);
        Action delete = FileUtil.getConfigObject("Actions/Edit/de-cebitec-mgx-gui-actions-DeleteAssembly.instance", Action.class);
        return new Action[]{annotate, delete};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Assembly.png");
        setShortDescription(getToolTipText(getContent()));
    }
}
