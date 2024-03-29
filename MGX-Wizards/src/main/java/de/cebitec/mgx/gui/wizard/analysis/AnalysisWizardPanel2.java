package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.wizard.analysis.misc.BooleanPanel;
import de.cebitec.mgx.gui.wizard.analysis.misc.ComboBoxPanel;
import de.cebitec.mgx.gui.wizard.analysis.misc.FileChooserPanel;
import de.cebitec.mgx.gui.wizard.analysis.misc.TextFieldPanel;
import de.cebitec.mgx.gui.wizard.analysis.misc.ValueHolderI;
import de.cebitec.mgx.gui.wizard.analysis.validator.BooleanValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.ByteValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.DoubleValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.FilenameValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.IntegerValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.LongValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.MultipleChoiceValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.SByteValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.StringValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.ULongValidator;
import de.cebitec.mgx.gui.wizard.analysis.validator.ValidatorI;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private final AnalysisVisualPanel2 component;
    private final MGXMasterI master;
    private JobParameterI parameter = null;
    private WizardDescriptor model = null;
    private boolean isValid = false;
    private ValidatorI validator = null;
    private ValueHolderI valueHolder = null;
    private final EventListenerList listeners = new EventListenerList();
    public static final String PROP_PARAM = "propParam";
    //
    private final List<MGXReferenceI> references;

    public AnalysisWizardPanel2(MGXMasterI master, List<MGXReferenceI> references) {
        this.master = master;
        this.references = references;
        component = new AnalysisVisualPanel2();
    }

    @Override
    public AnalysisVisualPanel2 getComponent() {
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

    @Override
    public void readSettings(WizardDescriptor settings) {
        model = settings;
        AnalysisVisualPanel2 c = getComponent();
        c.addPropertyChangeListener(this);
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        model = settings;
        String storeVal = validator.getValue() == null ? null : validator.getValue();
        parameter.setParameterValue(storeVal); //getComponent().getValue());
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
        NotificationLineSupport nls = null;
        if (model != null) {
            nls = model.getNotificationLineSupport();
            nls.clearMessages();
        }
        if (parameter.isOptional() && (valueHolder.getValue() == null || valueHolder.getValue().isEmpty())) {
            return true;
        }

        String input = valueHolder.getValue() == null ? null : valueHolder.getValue();
        boolean newValue = validator.validate(input);

        if (!newValue && nls != null) {
            nls.setErrorMessage(validator.getError());
        }
        return newValue;
    }

    public void setJobParameter(JobParameterI jp) {
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
            validator.validate(jp.getDefaultValue());
            String val = validator.getValue();
            if (val != null) {
                valueHolder.setValue(val);
            }
        }
        if (jp.getParameterValue() != null) {
            // same as above
            validator.validate(jp.getDefaultValue());
            String val = validator.getValue();
            if (val != null) {
                valueHolder.setValue(val);
            }
        }
        getComponent().setInputComponent(valueHolder);
    }

    private Pair<? extends ValueHolderI, ? extends ValidatorI> getValidator(JobParameterI jp) {
        switch (jp.getType()) {
            case "ConfigByte":
                return new Pair<>(new TextFieldPanel(), new ByteValidator());
            case "ConfigDouble":
                return new Pair<>(new TextFieldPanel(), new DoubleValidator());
            case "ConfigEnumeration`1":
                return new Pair<>(new ComboBoxPanel(jp, jp.getChoices().keySet()), new MultipleChoiceValidator<>(jp));
            case "ConfigFile":
                return new Pair<>(new FileChooserPanel(master), new FilenameValidator());
            case "ConfigInteger":
                return new Pair<>(new TextFieldPanel(), new IntegerValidator());
            case "ConfigLong":
                return new Pair<>(new TextFieldPanel(), new LongValidator());
            case "ConfigSByte":
                return new Pair<>(new TextFieldPanel(), new SByteValidator());
            case "ConfigSelection`2":
                return new Pair<>(new ComboBoxPanel(jp, jp.getChoices().keySet()), new MultipleChoiceValidator<>(jp));
            case "ConfigString":
                return new Pair<>(new TextFieldPanel(), new StringValidator());
            case "ConfigULong":
                return new Pair<>(new TextFieldPanel(), new ULongValidator());
            case "ConfigBoolean":
                return new Pair<>(new BooleanPanel(jp), new BooleanValidator());
            case "ConfigMGXReference":
                isValid = !references.isEmpty();
                Collection<String> tmp = new ArrayList<>();
                if (references != null) {
                    for (MGXReferenceI r : references) {
                        tmp.add(r.getName());
                    }
                }
                return new Pair<>(new ComboBoxPanel(jp, tmp), new MultipleChoiceValidator<>(jp, references));
            default:
                // uncheckable configuration type
                return new Pair<>(new TextFieldPanel(), new StringValidator());
        }
    }

    public String getName() {
        return "Configure parameters";
    }
}
