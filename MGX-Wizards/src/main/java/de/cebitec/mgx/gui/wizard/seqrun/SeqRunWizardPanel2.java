package de.cebitec.mgx.gui.wizard.seqrun;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class SeqRunWizardPanel2 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SeqRunVisualPanel2 component;
    private WizardDescriptor model = null;
    private boolean isValid = true;
    private final EventListenerList listeners = new EventListenerList();

    @Override
    public SeqRunVisualPanel2 getComponent() {
        if (component == null) {
            component = new SeqRunVisualPanel2();
            component.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(1));
        }
        return component;
    }

    public String getName() {
        return "Sequence data";
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
        SeqRunVisualPanel2 c = getComponent();
        c.addPropertyChangeListener(this);
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        model = settings;
        SeqRunVisualPanel2 c = getComponent();
        model.putProperty(SeqRunVisualPanel2.PROP_SEQFILE, c.getSelectedFile());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        isValid = checkValidity();
        fireChangeEvent(this, oldState, isValid);
    }

    private boolean checkValidity() {
        isValid = true;
        SeqRunVisualPanel2 c = getComponent();
        File test = c.getSelectedFile();
        if (test == null || test.isDirectory() || !test.exists()) {
            isValid = false;
        }

        return isValid;
    }
}
