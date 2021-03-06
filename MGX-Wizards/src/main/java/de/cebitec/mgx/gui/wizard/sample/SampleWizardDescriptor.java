package de.cebitec.mgx.gui.wizard.sample;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SampleI;
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

    private SampleI sample = null;
    private final MGXMasterI master;

    public SampleWizardDescriptor(MGXMasterI master) {
        this.master = master;
        List<Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(p1);
        panels.add(p2);
        this.setPanelsAndSettings(new ArrayIterator<>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Sample wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName(), p2.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);

    }

    public SampleWizardDescriptor(MGXMasterI master, SampleI s) {
        this(master);
        this.sample = s;
        putProperty(SampleVisualPanel1.PROP_COLLECTIONDATE, s.getCollectionDate());
        putProperty(SampleVisualPanel2.PROP_MATERIAL, s.getMaterial());
        putProperty(SampleVisualPanel2.PROP_TEMPERATURE, s.getTemperature());
        putProperty(SampleVisualPanel2.PROP_VOLUME, s.getVolume());
        putProperty(SampleVisualPanel2.PROP_VOLUME_UNIT, s.getVolumeUnit());
        p1.setProperties(this);
        p2.setProperties(this);
    }

    public Date getCollectionDate() {
        return (Date) getProperty(SampleVisualPanel1.PROP_COLLECTIONDATE);
    }

    public String getSampleMaterial() {
        return (String) getProperty(SampleVisualPanel2.PROP_MATERIAL);
    }

    public Double getTemperature() {
        return (Double) getProperty(SampleVisualPanel2.PROP_TEMPERATURE);
    }

    public Integer getVolume() {
        return (Integer) getProperty(SampleVisualPanel2.PROP_VOLUME);
    }

    public String getVolumeUnit() {
        return (String) getProperty(SampleVisualPanel2.PROP_VOLUME_UNIT);
    }

    public SampleI getSample() {
        // only usable when editing

        sample.setCollectionDate((Date) getProperty(SampleVisualPanel1.PROP_COLLECTIONDATE))
                .setMaterial((String) getProperty(SampleVisualPanel2.PROP_MATERIAL))
                .setTemperature((Double) getProperty(SampleVisualPanel2.PROP_TEMPERATURE))
                .setVolume((Integer) getProperty(SampleVisualPanel2.PROP_VOLUME))
                .setVolumeUnit((String) getProperty(SampleVisualPanel2.PROP_VOLUME_UNIT));
        return sample;
    }
}
