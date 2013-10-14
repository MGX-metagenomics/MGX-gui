package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Tool;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class AnalysisWizardPanel3 implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the component from this class, just use getComponent().
     */
    private AnalysisVisualPanel3 component;
    private final MGXMaster master;
    private final Set<Reference> references;

    public AnalysisWizardPanel3(MGXMaster master, Set<Reference> refs) {
        this.master = master;
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
    public void readSettings(WizardDescriptor wiz) {
        model = wiz;
        Tool tool = (Tool) model.getProperty(AnalysisWizardIterator.PROP_TOOL);
        List<JobParameter> params = (List<JobParameter>) model.getProperty(AnalysisWizardIterator.PROP_PARAMETERS);
        getComponent().setToolName(tool.getName());
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
