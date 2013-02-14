package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.wizard.analysis.validator.DoubleValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.IntegerValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.LongValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.StringValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.ValidatorI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.NotificationLineSupport;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class AnalysisWizardPanel2 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the component from this class, just use getComponent().
     */
    private AnalysisVisualPanel2 component;
    private MGXMaster master = null;
    private JobParameter parameter = null;
    private WizardDescriptor model = null;
    private boolean isValid = false;
    private final EventListenerList listeners = new EventListenerList();

    @Override
    public AnalysisVisualPanel2 getComponent() {
        if (component == null) {
            component = new AnalysisVisualPanel2();
        }
        return component;
    }

    public void setMaster(MGXMaster master) {
        this.master = master;
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
        AnalysisVisualPanel2 c = getComponent();
        c.addPropertyChangeListener(this);
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        model = settings;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        isValid = checkValidity();
        if (oldState != isValid) {
            fireChangeEvent(this, oldState, isValid);
        }
    }

    private boolean checkValidity() {
        isValid = true;
        if (parameter.isOptional()) {
            return true;
        }
        NotificationLineSupport nls = model.getNotificationLineSupport();
        nls.setErrorMessage("");
        return isValid;
    }

    public void setJobParameter(JobParameter jp) {
        parameter = jp;
        AnalysisVisualPanel2 avp = getComponent();
        String title = new StringBuilder(parameter.getDisplayName())
                .append(": ")
                .append(parameter.getUserName())
                .toString();
        avp.setTitle(title);
        avp.setDescription(parameter.getUserDescription());
        avp.setOptional(parameter.isOptional());

        ValidatorI validator = getValidator(parameter.getType());
    }

    private ValidatorI getValidator(String type) {
        switch (type) {
            case "ConfigByte":
                return null;
            case "ConfigDouble":
                return new DoubleValidator();
            case "ConfigEnumeration`1":
                return null;
            case "ConfigFile":
                return null;
            case "ConfigInteger":
                return new IntegerValidator();
            case "ConfigLong":
                return new LongValidator();
            case "ConfigSByte":
                return null;
            case "ConfigSelection`2":
                return null;
            case "ConfigString":
                return new StringValidator();
            case "ConfigULong":
                return null;
            case "ConfigBoolean":
                return null;
            default:
                // uncheckable configuration type
                return new StringValidator();
        }
    }
}
