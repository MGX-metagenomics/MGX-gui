package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import java.awt.Component;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;

public final class AnalysisWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor> {

    public static final String PROP_TOOL = "tool";
    public static final String PROP_TOOLTYPE = "tooltype";
    public static final String PROP_PARAMFETCHER = "paramFetcher";
    public static final String PROP_PARAMETERS = "toolParameters";
    public static final String PROP_JOB = "job";
    // Example of invoking this wizard:
    // @ActionID(category="...", id="...")
    // @ActionRegistration(displayName="...")
    // @ActionReference(path="Menu/...")
    // public static ActionListener run() {
    //     return new ActionListener() {
    //         @Override public void actionPerformed(ActionEvent e) {
    //             WizardDescriptor wiz = new WizardDescriptor(new AnalysisWizardIterator());
    //             // {0} will be replaced by WizardDescriptor.Panel.getComponent().getName()
    //             // {1} will be replaced by WizardDescriptor.Iterator.name()
    //             wiz.setTitleFormat(new MessageFormat("{0} ({1})"));
    //             wiz.setTitle("...dialog title...");
    //             if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
    //                 ...do something...
    //             }
    //         }
    //     };
    // }
    //private int index;
    private final MGXMaster master;
    private WizardDescriptor wd = null;
    //

    private enum State {

        INIT,
        SET_PARAMETERS,
        OVERVIEW;
    };
    private State nextState;

    public AnalysisWizardIterator(MGXMaster master) {
        this.master = master;
    }

    public void setWizardDescriptor(WizardDescriptor wd) {
        this.wd = wd;
        createPanels();
        nextState = State.INIT;
    }
    AnalysisWizardPanel1 p1 = new AnalysisWizardPanel1();
    AnalysisWizardPanel2 p2 = new AnalysisWizardPanel2();
    AnalysisWizardPanel3 p3 = new AnalysisWizardPanel3();

    private void createPanels() {

        p1.setMaster(master);
        //
        p2.setMaster(master);
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
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        if (currentParameterIdx == -1) {
            wd.putProperty(PROP_PARAMETERS, null);
            return p1;
        }
        switch (nextState) {
            case INIT:
                return p1;
            case SET_PARAMETERS:
                // update p2 with current data
                JobParameter jp = params.get(currentParameterIdx);
                p2.setJobParameter(jp);
                return p2;
            case OVERVIEW:
                return p3;
            default:
                assert false;
                return null;

        }
    }

    @Override
    public String name() {
        return "FOOO"; //index + 1 + ". from " + panels.size();
    }

    @Override
    public boolean hasNext() {
        return nextState != State.OVERVIEW;
    }

    @Override
    public boolean hasPrevious() {
        return nextState != State.INIT;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        currentParameterIdx++;
        updateState();
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        currentParameterIdx--;
        updateState();
    }
    private int currentParameterIdx = -1;
    private List<JobParameter> params = null;

    private void updateState() {
        if (wd.getProperty(PROP_TOOL) == null) {
            nextState = State.INIT;
       //     cs.fireChange();
            return;
        }
        if (wd.getProperty(PROP_PARAMETERS) != null) {
            if (params == null) {
                // start setting parameters for this tool..
                params = (List<JobParameter>) wd.getProperty(PROP_PARAMETERS);
                
                // make sure parameters are initially all unset
                for (JobParameter j : params) {
                    assert j.getParameterValue() == null;
                }
                nextState = State.SET_PARAMETERS;
                currentParameterIdx = 0;
            }

            // check if all non-optional parameters are set
            boolean allParamsSet = true;
            for (JobParameter j : params) {
                if (!j.isOptional()) {
                    if (j.getParameterValue() == null) {
                        allParamsSet = false;
                        break;
                    }
                }
            }

            if (allParamsSet) {
                nextState = State.OVERVIEW;
             //   cs.fireChange();
                return;
            }
        }
        if (wd.getProperty(PROP_JOB) != null) {
            nextState = State.OVERVIEW;
          //  cs.fireChange();
        }
    }
    //private ChangeSupport cs = new ChangeSupport(this);

    @Override
    public void addChangeListener(ChangeListener l) {
       // cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
       // cs.removeChangeListener(l);
    }
}
