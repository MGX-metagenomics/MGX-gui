package de.cebitec.mgx.gui.wizard.habitat;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.HabitatI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class HabitatWizardDescriptor extends WizardDescriptor {

    private HabitatWizardPanel1 p1 = new HabitatWizardPanel1();
    private HabitatWizardPanel2 p2 = new HabitatWizardPanel2();
    private HabitatI habitat = null;
    public static final String INVOCATION_MODE = "invocationMode";
    public static final String CREATE_MODE = "CREATE";
    public static final String EDIT_MODE = "EDIT";

    public HabitatWizardDescriptor() {
        List<Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(p1);
        panels.add(p2);
        this.setPanelsAndSettings(new ArrayIterator<>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Habitat wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName(), p2.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
        putProperty(HabitatWizardDescriptor.INVOCATION_MODE, CREATE_MODE);
        setData();
    }

    public HabitatWizardDescriptor(HabitatI h) {
        this();
        this.habitat = h;
        putProperty(HabitatVisualPanel1.PROP_NAME, habitat.getName());
        putProperty(HabitatVisualPanel1.PROP_BIOME, habitat.getBiome());
        putProperty(HabitatVisualPanel1.PROP_LATITUDE, habitat.getLatitude());
        putProperty(HabitatVisualPanel1.PROP_LONGITUDE, habitat.getLongitude());
        putProperty(HabitatVisualPanel2.PROP_DESCRIPTION, habitat.getDescription());
        putProperty(HabitatWizardDescriptor.INVOCATION_MODE, EDIT_MODE);

        p1.setProperties(this);
        p2.setProperties(this);

    }

    private void setData() {
        final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws MGXException {
                p1.setHabitat(m.Habitat().fetchall());
                return null;
            }
        };
        sw.execute();
        try {
            sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public String getHabitatName() {
        return (String) getProperty(HabitatVisualPanel1.PROP_NAME);
    }

    public String getHabitatBiome() {
        return (String) getProperty(HabitatVisualPanel1.PROP_BIOME);
    }

    public double getHabitatLatitude() {
        return (Double) getProperty(HabitatVisualPanel1.PROP_LATITUDE);
    }

    public double getHabitatLongitude() {
        return (Double) getProperty(HabitatVisualPanel1.PROP_LONGITUDE);
    }
    
    public String getHabitatDescription() {
        return (String) getProperty(HabitatVisualPanel2.PROP_DESCRIPTION);
    }

    public HabitatI getHabitat(MGXMasterI m) {
        // only used when editing a habitat

        habitat.setName((String) getProperty(HabitatVisualPanel1.PROP_NAME))
                .setBiome((String) getProperty(HabitatVisualPanel1.PROP_BIOME))
                .setLatitude((Double) getProperty(HabitatVisualPanel1.PROP_LATITUDE))
                .setLongitude((Double) getProperty(HabitatVisualPanel1.PROP_LONGITUDE))
                .setDescription((String) getProperty(HabitatVisualPanel2.PROP_DESCRIPTION));
        return habitat;
    }
}
