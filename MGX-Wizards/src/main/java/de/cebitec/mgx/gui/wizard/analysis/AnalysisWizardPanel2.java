package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.wizard.analysis.misc.BooleanPanel;
import de.cebitec.mgx.gui.wizard.analysis.misc.ComboBoxPanel;
import de.cebitec.mgx.gui.wizard.analysis.misc.FileChooserPanel;
import de.cebitec.mgx.gui.wizard.analysis.misc.TextFieldPanel;
import de.cebitec.mgx.gui.wizard.analysis.misc.ValueHolderI;
import de.cebitec.mgx.gui.wizard.analysis.validator.BooleanValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.ByteValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.DoubleValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.IntegerValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.LongValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.MultipleChoiceValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.SByteValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.StringValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.ULongValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.ValidatorI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.NotificationLineSupport;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class AnalysisWizardPanel2 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private AnalysisVisualPanel2 component;
    private MGXMaster master = null;
    private JobParameter parameter = null;
    private WizardDescriptor model = null;
    private boolean isValid = false;
    private ValidatorI validator = null;
    private ValueHolderI<String> valueHolder = null;
    private final EventListenerList listeners = new EventListenerList();
    public static final String PROP_PARAM = "propParam";

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
        parameter.setParameterValue(getComponent().getValue());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;
        isValid = checkValidity();
        // if (oldState != isValid) {
        fireChangeEvent(this, oldState, isValid);
        // }
    }

    private boolean checkValidity() {
        //isValid = true;
        NotificationLineSupport nls = model.getNotificationLineSupport();
        nls.clearMessages();
        if (parameter.isOptional() && valueHolder.getValue().isEmpty()) {
            return true;
        }

        boolean newValue = validator.validate(valueHolder.getValue());

        if (!newValue) {
            nls.setErrorMessage(validator.getError());
        }
        return newValue;
    }

    public void setJobParameter(JobParameter jp) {
        if (jp == parameter) {
            return;
        }
        parameter = jp;
        AnalysisVisualPanel2 avp = getComponent();
        String title = new StringBuilder(parameter.getDisplayName())
                .append(": ")
                .append(parameter.getUserName())
                .toString();
        avp.setTitle(title);
        avp.setDescription(parameter.getUserDescription());
        avp.setOptional(parameter.isOptional());


        Pair<? extends ValueHolderI, ? extends ValidatorI> p = getValidator(parameter);
        valueHolder = p.getFirst();
        validator = p.getSecond();
        if (jp.getDefaultValue() != null) {
            valueHolder.setValue(jp.getDefaultValue());
        }
        if (jp.getParameterValue() != null) {
            valueHolder.setValue(jp.getParameterValue());
        }
        getComponent().setInputComponent(valueHolder);
    }

    private Pair<? extends ValueHolderI, ? extends ValidatorI> getValidator(JobParameter jp) {
        switch (jp.getType()) {
            case "ConfigByte":
                return new Pair<>(new TextFieldPanel(), new ByteValidator());
            case "ConfigDouble":
                return new Pair<>(new TextFieldPanel(), new DoubleValidator());
            case "ConfigEnumeration`1":
                return new Pair<>(new ComboBoxPanel(jp), new MultipleChoiceValidator(jp));
            case "ConfigFile":
                return new Pair<>(new FileChooserPanel(master), new StringValidator());
            case "ConfigInteger":
                return new Pair<>(new TextFieldPanel(), new IntegerValidator());
            case "ConfigLong":
                return new Pair<>(new TextFieldPanel(), new LongValidator());
            case "ConfigSByte":
                return new Pair<>(new TextFieldPanel(), new SByteValidator());
            case "ConfigSelection`2":
                return new Pair<>(new ComboBoxPanel(jp), new MultipleChoiceValidator(jp));
            case "ConfigString":
                return new Pair<>(new TextFieldPanel(), new StringValidator());
            case "ConfigULong":
                return new Pair<>(new TextFieldPanel(), new ULongValidator());
            case "ConfigBoolean":
                return new Pair<>(new BooleanPanel(jp), new BooleanValidator());
            case "ConfigMGXReference":
                final Map<Reference, String> refs = new HashMap<>();
                NonEDT.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        Iterator<Reference> iter = master.Reference().fetchall();
                        while (iter.hasNext()) {
                            Reference r = iter.next();
                            refs.put(r, String.valueOf(r.getId()));
                        }
                    }
                });

                return new Pair<>(new ComboBoxPanel(jp, refs), new MultipleChoiceValidator(jp, refs));
            default:
                // uncheckable configuration type
                return new Pair<>(new TextFieldPanel(), new StringValidator());
        }
    }

    public String getName() {
        return "Configure parameters";
    }
}
