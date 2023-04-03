package de.cebitec.mgx.gui.seqexporter;

import de.cebitec.mgx.api.groups.GroupI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class ExportSeqWizardPanel2<U> implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private ExportSeqVisualPanel2<U> component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public ExportSeqVisualPanel2<U> getComponent() {
        if (component == null) {
            component = new ExportSeqVisualPanel2<>();
        }
        return component;
    }

    @SuppressWarnings("unchecked")
    public void setGroup(GroupI<U> vg) {
        getComponent().setVisualizationGroup(vg);
    }

    public File getSelectedFile() {
        return getComponent().getSelectedFile();
    }

    public boolean hasQuality() {
        return getComponent().hasQuality();
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        return valid; //getSelectedFile() != null;
    }
    private boolean valid = false;

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
        ExportSeqVisualPanel2<U> c = getComponent();
        c.addPropertyChangeListener(this);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean old = valid;
        valid = getSelectedFile() != null;
        fireChangeEvent(this, old, valid);
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
