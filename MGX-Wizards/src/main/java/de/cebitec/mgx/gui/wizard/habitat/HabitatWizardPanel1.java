package de.cebitec.mgx.gui.wizard.habitat;

import de.cebitec.mgx.api.model.HabitatI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class HabitatWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private HabitatVisualPanel1 component;
    private WizardDescriptor model = null;
    private boolean isValid = false;
    private final EventListenerList listeners = new EventListenerList();
    private Collection<HabitatI> allHabitat;
    
    
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public HabitatVisualPanel1 getComponent() {
        if (component == null) {
            component = new HabitatVisualPanel1();
            component.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(0));
        }
        return component;
    }
    
    public String getName() {
        return "Type and location";
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

    @Override
    public void readSettings(WizardDescriptor settings) {
        model = settings;
        HabitatVisualPanel1 c = getComponent();
        c.addPropertyChangeListener(this);
    }
    
    public void setProperties(WizardDescriptor settings) {
        model = settings;
        HabitatVisualPanel1 c = getComponent();
        c.setHabitatName((String)model.getProperty(HabitatVisualPanel1.PROP_NAME));
        c.setBiome((String)model.getProperty(HabitatVisualPanel1.PROP_BIOME));
        if (model.getProperty(HabitatVisualPanel1.PROP_LATITUDE) != null) {
            c.setGPSLatitude((Double)model.getProperty(HabitatVisualPanel1.PROP_LATITUDE));
            c.setGPSLongitude((Double)model.getProperty(HabitatVisualPanel1.PROP_LONGITUDE));
        }
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        model = settings;
        HabitatVisualPanel1 c = getComponent();
        model.putProperty(HabitatVisualPanel1.PROP_NAME, c.getHabitatName());
        model.putProperty(HabitatVisualPanel1.PROP_BIOME, c.getBiome());
        model.putProperty(HabitatVisualPanel1.PROP_LATITUDE, c.getGPSLatitude());
        model.putProperty(HabitatVisualPanel1.PROP_LONGITUDE, c.getGPSLongitude());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        isValid = checkValidity();
        fireChangeEvent(this, oldState, isValid);
    }

    private boolean checkValidity() {
        isValid = true;
        
        HabitatVisualPanel1 c = getComponent();
        String test = c.getHabitatName();
        if (test == null || "".equals(test)) {
            model.getNotificationLineSupport().setErrorMessage("Please enter a habitat name.");
            isValid = false;
        }

        test = c.getBiome();
        if (test == null || "".equals(test)) {
            model.getNotificationLineSupport().setErrorMessage("Please enter biome type.");
            isValid = false;
        }

        Double d = c.getGPSLatitude();
        if (d == null) {
            model.getNotificationLineSupport().setErrorMessage("Please choose a location.");
            isValid = false;
        }
        
        d = c.getGPSLongitude();
        if (d == null) {
            model.getNotificationLineSupport().setErrorMessage("Please choose a location.");
            isValid = false;
        }
        boolean alreadyExists = checkDuplicate(c.getHabitatName());
        if (alreadyExists) {
            model.getNotificationLineSupport().setErrorMessage("Habitat with same name already exists.");
            isValid = false;
        } 
        
        if(isValid){
             model.getNotificationLineSupport().clearMessages();
        }
        
        
        return isValid;
    }
    
    public void setHabitat(Iterator<HabitatI> iter) {
        allHabitat = new ArrayList<>();
        while (iter.hasNext()) {
            allHabitat.add(iter.next());
        }
    }

    private boolean checkDuplicate(String name) {
        // prevent creating duplicate extract names
        boolean alreadyExists = false;
        if (model.getProperty(HabitatWizardDescriptor.INVOCATION_MODE).equals(HabitatWizardDescriptor.CREATE_MODE)) {
            for (HabitatI habitat : allHabitat) {
                if (habitat.getName().equals(name)) {
                    alreadyExists = true;
                }
            }
        } else if(model.getProperty(HabitatWizardDescriptor.INVOCATION_MODE).equals(HabitatWizardDescriptor.EDIT_MODE)){
            for (HabitatI habitat : allHabitat) {
                if (habitat.getName().equals(name) && !habitat.getName().equals(model.getProperty(HabitatVisualPanel1.PROP_NAME))) {
                    alreadyExists = true;
                }
            }
        }
        return alreadyExists;
    }
}
