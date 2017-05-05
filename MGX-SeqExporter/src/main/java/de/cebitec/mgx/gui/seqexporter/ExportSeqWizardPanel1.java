package de.cebitec.mgx.gui.seqexporter;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class ExportSeqWizardPanel1<T extends Number> implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private ExportSeqVisualPanel1<T> component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public ExportSeqVisualPanel1<T> getComponent() {
        if (component == null) {
            component = new ExportSeqVisualPanel1<>();
            component.addPropertyChangeListener(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public boolean isValid() {
        //return getSelectedAttributes().size() > 0;
        
        /*
         * panel might be used repeatedly when exporting data for several groups;
         * thus, no selected attributes are valid and indicate this group to
         * be skipped from the export
        */
        return true;
    }

    public void setDistribution(DistributionI<T> d) {
        getComponent().setDistribution(d);
    }

    public Set<AttributeI> getSelectedAttributes() {
        return getComponent().getSelectedAttributes();
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireChangeEvent(this, !isValid(), isValid());
    }

    protected final void fireChangeEvent(Object src, boolean old, boolean newState) {
        if (old != newState) {
            ChangeEvent ev = new ChangeEvent(src);
            for (ChangeListener cl : listeners.getListeners(ChangeListener.class)) {
                cl.stateChanged(ev);
            }
        }
    }
    private final EventListenerList listeners = new EventListenerList();

    @Override
    public final void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }
}
