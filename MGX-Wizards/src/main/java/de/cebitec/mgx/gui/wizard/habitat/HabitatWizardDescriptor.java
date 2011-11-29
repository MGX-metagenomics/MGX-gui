package de.cebitec.mgx.gui.wizard.habitat;

import de.cebitec.mgx.gui.datamodel.Habitat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.openide.WizardDescriptor;

/**
 *
 * @author sjaenick
 */
public class HabitatWizardDescriptor extends WizardDescriptor {

    private HabitatWizardPanel1 p1 = new HabitatWizardPanel1();
    private HabitatWizardPanel2 p2 = new HabitatWizardPanel2();

    public HabitatWizardDescriptor() {
        List<Panel<WizardDescriptor>> panels = new ArrayList<Panel<WizardDescriptor>>();
        panels.add(p1);
        panels.add(p2);
        this.setPanelsAndSettings(new ArrayIterator<WizardDescriptor>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Habitat wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName(), p2.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
    }
    
    public HabitatWizardDescriptor(Habitat h) {
        this();
        putProperty(HabitatVisualPanel1.PROP_NAME, h.getName());
        putProperty(HabitatVisualPanel1.PROP_BIOME, h.getBiome());
        putProperty(HabitatVisualPanel1.PROP_LATITUDE, h.getLatitude());
        putProperty(HabitatVisualPanel1.PROP_LONGITUDE, h.getLongitude());
        //
        putProperty(HabitatVisualPanel2.PROP_DESCRIPTION, h.getDescription());
    }
}
