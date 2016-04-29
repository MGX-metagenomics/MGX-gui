package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class AnalysisWizardPanel3 implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the component from this class, just use getComponent().
     */
    private AnalysisVisualPanel3 component;
    private final List<MGXReferenceI> references;

    public AnalysisWizardPanel3(List<MGXReferenceI> refs) {
        references = refs;
    }

    @Override
    public AnalysisVisualPanel3 getComponent() {
        if (component == null) {
            component = new AnalysisVisualPanel3(references);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public boolean isValid() {
        return true;
    }
    private WizardDescriptor model = null;

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readSettings(WizardDescriptor wiz) {
        model = wiz;
        String toolName = (String) model.getProperty(AnalysisWizardIterator.PROP_TOOLNAME);
        List<JobParameterI> params = (List<JobParameterI>) model.getProperty(AnalysisWizardIterator.PROP_PARAMETERS);
        getComponent().setToolName(toolName);
        getComponent().setParameters(params);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        model = wiz;
    }

    public String getName() {
        return "Confirm";
    }
}
