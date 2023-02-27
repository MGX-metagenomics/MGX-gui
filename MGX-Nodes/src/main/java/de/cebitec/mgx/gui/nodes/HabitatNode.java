package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.nodeactions.AddSample;
import de.cebitec.mgx.gui.actions.DeleteHabitat;
import de.cebitec.mgx.gui.actions.EditHabitat;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.gui.nodefactory.SampleNodeFactory;
import java.awt.Image;
import javax.swing.Action;
import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class HabitatNode extends MGXNodeBase<HabitatI> {

    public HabitatNode(HabitatI h) {
        this(h, new SampleNodeFactory(h));
    }

    private HabitatNode(HabitatI h, SampleNodeFactory snf) {
        super(Children.create(snf, true), Lookups.fixed(h.getMaster(), h), h);
        super.setDisplayName(h.getName());
        super.setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.svg");
        super.setShortDescription(getToolTipText(h));
    }

    @Override
    public Image getIcon(int type) {
        Image image = super.getIcon(type);
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
    }

    private String getToolTipText(HabitatI h) {
        return new StringBuilder("<html>").append("<b>Habitat: </b>")
                .append(escapeHtml4(h.getName()))
                .append("<br><hr><br>")
                .append("Biome: ").append(escapeHtml4(h.getBiome())).append("<br>")
                .append("Location: ").append(Double.toString(h.getLatitude()))
                .append(" / ").append(Double.toString(h.getLongitude()))
                .append("</html>").toString();
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new EditHabitat(), new DeleteHabitat(), new AddSample()};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Habitat.png");
        setShortDescription(getToolTipText(getContent()));
    }
}
