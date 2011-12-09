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
    
    private Habitat habitat = null;
    
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
        this.habitat = h;
        putProperty(HabitatVisualPanel1.PROP_NAME, habitat.getName());
        putProperty(HabitatVisualPanel1.PROP_BIOME, habitat.getBiome());
        putProperty(HabitatVisualPanel1.PROP_LATITUDE, habitat.getLatitude());
        putProperty(HabitatVisualPanel1.PROP_LONGITUDE, habitat.getLongitude());
        putProperty(HabitatVisualPanel1.PROP_ALTITUDE, habitat.getAltitude());
        putProperty(HabitatVisualPanel2.PROP_DESCRIPTION, habitat.getDescription());
        p1.setProperties(this);
        p2.setProperties(this);
    }
    
    public Habitat getHabitat() {
        if (habitat == null) {
            habitat = new Habitat();
        }
        
        habitat.setName((String)getProperty(HabitatVisualPanel1.PROP_NAME))
                .setBiome((String)getProperty(HabitatVisualPanel1.PROP_BIOME))
                .setLatitude((Double)getProperty(HabitatVisualPanel1.PROP_LATITUDE))
                .setLongitude((Double)getProperty(HabitatVisualPanel1.PROP_LONGITUDE))
                .setAltitude((Integer)getProperty(HabitatVisualPanel1.PROP_ALTITUDE))
                .setDescription((String)getProperty(HabitatVisualPanel2.PROP_DESCRIPTION));
        return habitat;
    }
}
