package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.ToolI;
import java.awt.Component;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;

public final class AnalysisWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor> {

    //public static final String PROP_TOOL = "tool";
    public static final String PROP_TOOLID = "toolid";
    public static final String PROP_TOOLNAME = "toolName";
    public static final String PROP_TOOLDESC = "toolDesc";
    public static final String PROP_TOOLAUTHOR = "toolAuthor";
    public static final String PROP_TOOL_URL = "toolUrl";
    public static final String PROP_TOOLVERSION = "toolVersion";
    public static final String PROP_TOOL_DEFINITION = "toolDefinition";
    public static final String PROP_TOOLTYPE = "tooltype";
    public static final String PROP_PARAMETERS = "toolParameters";
    //
    private WizardDescriptor wd = null;
    private int numPanels = 2;
    private int idx = 0;
    //
    private final AnalysisWizardPanel1 p1;
    private final AnalysisWizardPanel2 p2;
    private final AnalysisWizardPanel3 p3;

    public AnalysisWizardIterator(MGXMasterI master, List<MGXReferenceI> references, List<ToolI> projectTools, List<ToolI> repoTools) {
        p1 = new AnalysisWizardPanel1(master, projectTools, repoTools);
        p2 = new AnalysisWizardPanel2(master, references);
        p3 = new AnalysisWizardPanel3(references);
    }

    public void setWizardDescriptor(WizardDescriptor wd) {
        this.wd = wd;
        wd.putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName(), p2.getName(), p3.getName()});
        createPanels();
    }

    @SuppressWarnings("unchecked")
    private void createPanels() {

        //p1.setMaster(master);
        p1.setWizardDescriptor(wd);
        //
        //p2.setMaster(master);
        //
        WizardDescriptor.Panel<WizardDescriptor>[] panels;
        panels = new WizardDescriptor.Panel[]{p1, p2, p3};
        int i = 0;
        for (WizardDescriptor.Panel p : panels) {
            Component c = p.getComponent();
            if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i++);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, c.getName());
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        if (idx == 0) {
            return p1;
        } else if (idx == numPanels - 1) {
            return p3;
        }

        List<JobParameterI> params = (List<JobParameterI>) wd.getProperty(PROP_PARAMETERS);
        JobParameterI jp = params.get(idx - 1);
        p2.setJobParameter(jp);
        return p2;
    }

    @Override
    public String name() {
        return "FOOO"; //index + 1 + ". from " + panels.size();
    }

    @Override
    public boolean hasNext() {
        return idx < numPanels - 1;
    }

    @Override
    public boolean hasPrevious() {
        return idx > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        idx++;
        updateState();
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        idx--;
        updateState();
    }
    //private int currentParameterIdx = -1;

    @SuppressWarnings("unchecked")
    private void updateState() {
        if (wd.getProperty(PROP_PARAMETERS) != null) {
            List<JobParameterI> params = (List<JobParameterI>) wd.getProperty(PROP_PARAMETERS);
            numPanels = 2 + params.size();
        }
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
}
