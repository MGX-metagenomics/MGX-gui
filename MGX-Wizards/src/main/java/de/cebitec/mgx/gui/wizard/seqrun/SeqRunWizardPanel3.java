package de.cebitec.mgx.gui.wizard.seqrun;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class SeqRunWizardPanel3 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener{

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SeqRunVisualPanel3 component;
    private WizardDescriptor model = null;
    private boolean isValid = true;
    private final EventListenerList listeners = new EventListenerList();    

    @Override
    public SeqRunVisualPanel3 getComponent() {
        if (component == null) {
            component = new SeqRunVisualPanel3();
            component.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, Integer.valueOf(1));
        }
        return component;
    }

    public String getName() {
        return "Paired-End parameters";
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
    public void readSettings(WizardDescriptor settings) {
        model = settings;
        SeqRunVisualPanel3 c = getComponent();
        c.addPropertyChangeListener(this);
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        model = settings;
        SeqRunVisualPanel3 c = getComponent();
        model.putProperty(SeqRunVisualPanel3.PROP_QTHRESHOLD, c.getQualityThreshold());
        model.putProperty(SeqRunVisualPanel3.PROP_MINOVERLAP, c.getMinimalOverlap());
        model.putProperty(SeqRunVisualPanel3.PROP_MAXMISMATCHES, c.getMaximalMismatches());        
    }

    public void setProperties(WizardDescriptor settings) {
        model = settings;
        SeqRunVisualPanel3 c = getComponent();
        c.setQualityThreshold((int) model.getProperty(SeqRunVisualPanel3.PROP_QTHRESHOLD));
        c.setMinimalOverlap((int) model.getProperty(SeqRunVisualPanel3.PROP_MINOVERLAP));
        c.setMaximalMismatches((int) model.getProperty(SeqRunVisualPanel3.PROP_MAXMISMATCHES));
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
        listeners.add(ChangeListener.class, cl);
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        listeners.remove(ChangeListener.class, cl);
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
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        isValid = checkValidity();
        fireChangeEvent(this, oldState, isValid);
    }

    private boolean checkValidity() {
        return true;
    }
}
