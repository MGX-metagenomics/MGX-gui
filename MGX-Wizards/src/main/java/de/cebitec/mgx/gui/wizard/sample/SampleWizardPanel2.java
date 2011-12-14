/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.sample;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class SampleWizardPanel2 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SampleVisualPanel2 component;
    private WizardDescriptor model = null;
    private boolean isValid = false;
    private final EventListenerList listeners = new EventListenerList();

    @Override
    public SampleVisualPanel2 getComponent() {
        if (component == null) {
            component = new SampleVisualPanel2();
            component.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(0));
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public final void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }

    protected final void fireChangeEvent(Object src, boolean old, boolean newState) {
        if (old != newState) {
            ChangeEvent ev = new ChangeEvent(src);
            for (ChangeListener cl : listeners.getListeners(ChangeListener.class)) {
                cl.stateChanged(ev);
            }
        }
    }

    String getName() {
        return "Sample description";
    }

    @Override
    public void readSettings(WizardDescriptor settings) {
        model = settings;
        SampleVisualPanel2 c = getComponent();
        c.addPropertyChangeListener(this);
    }

    public void setProperties(WizardDescriptor settings) {
        model = settings;
        SampleVisualPanel2 c = getComponent();
        c.setMaterial((String) model.getProperty(SampleVisualPanel2.PROP_MATERIAL));
        c.setVolume((Integer) model.getProperty(SampleVisualPanel2.PROP_VOLUME));
        c.setVolumeUnit((String) model.getProperty(SampleVisualPanel2.PROP_VOLUME_UNIT));
        c.setTemperature((Double) model.getProperty(SampleVisualPanel2.PROP_TEMPERATURE));
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        model = settings;
        SampleVisualPanel2 c = getComponent();
        model.putProperty(SampleVisualPanel2.PROP_MATERIAL, c.getMaterial());
        if (!"".equals(c.getVolume())) {
            model.putProperty(SampleVisualPanel2.PROP_VOLUME, Integer.parseInt(c.getVolume()));
        }
        model.putProperty(SampleVisualPanel2.PROP_VOLUME_UNIT, c.getVolumeUnit());
        //
        Double temp = ConvertToKelvin();
        if (temp != null) {
            model.putProperty(SampleVisualPanel2.PROP_TEMPERATURE, temp);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        isValid = checkValidity();
        fireChangeEvent(this, oldState, isValid);
    }

    private boolean checkValidity() {
        isValid = true;

        SampleVisualPanel2 c = getComponent();
        String test = c.getMaterial();
        if (test == null || "".equals(test)) {
            isValid = false;
        }

        test = c.getVolumeUnit();
        if (test == null || "".equals(test)) {
            isValid = false;
        }

        test = c.getVolume();
        if (test == null || "".equals(test)) {
            isValid = false;
        } else {
            try {
                int parseInt = Integer.parseInt(test);
                if (parseInt <= 0) {
                    isValid = false;
                }
            } catch (NumberFormatException nfe) {
                isValid = false;
            }
        }

        test = c.getTemperature();
        if (test == null || "".equals(test)) {
            isValid = false;
        } else {
            try {
                Double.parseDouble(test);
                Double temp = ConvertToKelvin();
                // temperature below 0 K or above 573 K (== 300°C)
                if (temp == null || temp <= 0 || temp > 573) {
                    // exclude unlikely temperatures
                    isValid = false;
                }
            } catch (NumberFormatException nfe) {
                isValid = false;
            }
        }
        return isValid;
    }

    private Double ConvertToKelvin() {
        SampleVisualPanel2 c = getComponent();
        String t = c.getTemperature();
        if ("".equals(t)) {
            return null;
        }
        Double temp = Double.parseDouble(t);
        String tempunit = c.getTemperatureUnit();
        if ("°C".equals(tempunit)) {
            temp = temp + 273.15;
        } else if ("°F".equals(tempunit)) {
            temp = (temp - 32) * 5 / 9 + 273.15;
        } else if ("K".equals(tempunit)) {
        } else {
            throw new RuntimeException("Dont know about temperature in " + tempunit);
        }
        return temp;
    }
}
