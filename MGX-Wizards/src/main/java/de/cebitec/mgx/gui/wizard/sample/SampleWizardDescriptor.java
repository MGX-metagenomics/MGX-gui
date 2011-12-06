package de.cebitec.mgx.gui.wizard.sample;

import de.cebitec.mgx.gui.datamodel.Sample;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.openide.WizardDescriptor;

/**
 *
 * @author sjaenick
 */
public class SampleWizardDescriptor extends WizardDescriptor {

    private SampleWizardPanel1 p1 = new SampleWizardPanel1();
    private SampleWizardPanel2 p2 = new SampleWizardPanel2();
    
    private Sample sample = null;

    public SampleWizardDescriptor() {
        List<Panel<WizardDescriptor>> panels = new ArrayList<Panel<WizardDescriptor>>();
        panels.add(p1);
        panels.add(p2);
        this.setPanelsAndSettings(new ArrayIterator<WizardDescriptor>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Sample wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName(), p2.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
    }
    
    public SampleWizardDescriptor(Sample s) {
        this();
        this.sample = s; 
        putProperty(SampleVisualPanel1.PROP_COLLECTIONDATE, s.getCollectionDate());
        putProperty(SampleVisualPanel2.PROP_MATERIAL, s.getMaterial());
        putProperty(SampleVisualPanel2.PROP_TEMPERATURE, s.getTemperature());
        putProperty(SampleVisualPanel2.PROP_VOLUME, s.getVolume());
        putProperty(SampleVisualPanel2.PROP_VOLUME_UNIT, s.getVolumeUnit());
    }
    
    public Sample getSample() {
        if (sample == null) {
            sample = new Sample();
        }
        
        sample.setCollectionDate((Date)getProperty(SampleVisualPanel1.PROP_COLLECTIONDATE))
                .setMaterial((String)getProperty(SampleVisualPanel2.PROP_MATERIAL))
                .setTemperature((Integer)getProperty(SampleVisualPanel2.PROP_TEMPERATURE))
                .setVolume((Integer)getProperty(SampleVisualPanel2.PROP_VOLUME))
                .setVolumeUnit((String)getProperty(SampleVisualPanel2.PROP_VOLUME_UNIT));
        return sample;
    }
}
