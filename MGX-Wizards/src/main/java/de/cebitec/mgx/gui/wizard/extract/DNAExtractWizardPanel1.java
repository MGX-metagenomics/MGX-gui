package de.cebitec.mgx.gui.wizard.extract;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class DNAExtractWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private DNAExtractVisualPanel1 component;
    private WizardDescriptor model = null;
    private boolean isValid = true;
    private final EventListenerList listeners = new EventListenerList();

    @Override
    public DNAExtractVisualPanel1 getComponent() {
        if (component == null) {
            component = new DNAExtractVisualPanel1();
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

    public String getName() {
        return "Extraction protocol";
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
        DNAExtractVisualPanel1 c = getComponent();
        c.addPropertyChangeListener(this);
    }

    public void setProperties(WizardDescriptor settings) {
        model = settings;
        DNAExtractVisualPanel1 c = getComponent();
        c.setMethod((String) model.getProperty(DNAExtractVisualPanel1.PROP_METHOD));
        c.setProtocol((String) model.getProperty(DNAExtractVisualPanel1.PROP_PROTOCOL));
        c.setFiveprimer((String) model.getProperty(DNAExtractVisualPanel1.PROP_FIVEPRIMER));
        c.setThreeprimer((String) model.getProperty(DNAExtractVisualPanel1.PROP_THREEPRIMER));
        c.setTargetgene((String) model.getProperty(DNAExtractVisualPanel1.PROP_GENE));
        c.setTargetfragment((String) model.getProperty(DNAExtractVisualPanel1.PROP_FRAGMENT));
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        model = settings;
        DNAExtractVisualPanel1 c = getComponent();
        model.putProperty(DNAExtractVisualPanel1.PROP_METHOD, c.getMethod());
        model.putProperty(DNAExtractVisualPanel1.PROP_PROTOCOL, c.getProtocol());
        model.putProperty(DNAExtractVisualPanel1.PROP_FIVEPRIMER, c.getFiveprimer());
        model.putProperty(DNAExtractVisualPanel1.PROP_THREEPRIMER, c.getThreeprimer());
        model.putProperty(DNAExtractVisualPanel1.PROP_GENE, c.getTargetgene());
        model.putProperty(DNAExtractVisualPanel1.PROP_FRAGMENT, c.getTargetfragment());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        isValid = checkValidity();
        fireChangeEvent(this, oldState, isValid);
    }

    private boolean checkValidity() {
        isValid = true;

        DNAExtractVisualPanel1 c = getComponent();
        String test = c.getMethod();
        if (test == null || "".equals(test)) {
            isValid = false;
        }

        return isValid;
    }
}
