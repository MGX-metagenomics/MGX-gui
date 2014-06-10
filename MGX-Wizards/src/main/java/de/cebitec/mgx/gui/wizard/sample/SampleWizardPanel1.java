package de.cebitec.mgx.gui.wizard.sample;

import de.cebitec.mgx.api.model.SampleI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Date;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class SampleWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SampleVisualPanel1 component;
    private WizardDescriptor model = null;
    private boolean isValid = false;
    private final EventListenerList listeners = new EventListenerList();
    private Collection<SampleI> allSamples;
    
    @Override
    public SampleVisualPanel1 getComponent() {
        if (component == null) {
            component = new SampleVisualPanel1();
            component.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(1));
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
        return "Collection date";
    }

    @Override
    public void readSettings(WizardDescriptor settings) {
        model = settings;
        SampleVisualPanel1 c = getComponent();
        c.addPropertyChangeListener(this);
    }

    public void setProperties(WizardDescriptor settings) {
        model = settings;
        SampleVisualPanel1 c = getComponent();
        c.setCollectionDate((Date) model.getProperty(SampleVisualPanel1.PROP_COLLECTIONDATE));
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        model = settings;
        SampleVisualPanel1 c = getComponent();
        model.putProperty(SampleVisualPanel1.PROP_COLLECTIONDATE, c.getCollectionDate());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        isValid = checkValidity();
        fireChangeEvent(this, oldState, isValid);
    }

    private boolean checkValidity() {
        Date now = new Date(System.currentTimeMillis());
        Date selectedDate = getComponent().getCollectionDate();
        isValid = ((selectedDate != null)
                && (now.compareTo(selectedDate) >= 0));
       
        return isValid;
    }
}
