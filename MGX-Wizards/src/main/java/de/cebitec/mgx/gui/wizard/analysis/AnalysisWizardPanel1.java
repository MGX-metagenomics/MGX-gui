package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.ToolType;
import de.cebitec.mgx.api.model.JobParameterI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.gui.wizard.analysis.workers.ParameterRetriever;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.NotificationLineSupport;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class AnalysisWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private final AnalysisVisualPanel1 component;
    private WizardDescriptor model = null;
    private boolean isValid = false;
    private final EventListenerList listeners = new EventListenerList();
    //
    private final List<ToolI> projTools;
    private final List<ToolI> serverTools;
    private ToolI currentTool = null;
    private List<JobParameterI> currentParams = null;

    public AnalysisWizardPanel1(MGXMasterI master, List<ToolI> projectTools, List<ToolI> serverTools) {
        this.projTools = projectTools;
        this.serverTools = serverTools;
        component = new AnalysisVisualPanel1(master);
        component.addPropertyChangeListener(this);
        component.setProjectTools(projTools);
        component.setServerTools(serverTools);
    }

    public void setWizardDescriptor(WizardDescriptor wdesc) {
        model = wdesc;
    }

    public String getName() {
        return "Select tool";
    }

    @Override
    public AnalysisVisualPanel1 getComponent() {
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
        //model.putProperty(AnalysisWizardIterator.PROP_PARAMETERS, null);
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        AnalysisVisualPanel1 c = getComponent();

        model.putProperty(AnalysisWizardIterator.PROP_TOOL, currentTool);
        model.putProperty(AnalysisWizardIterator.PROP_TOOLTYPE, c.getToolType());
        model.putProperty(AnalysisWizardIterator.PROP_PARAMETERS, currentParams);
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;

        if (!evt.getPropertyName().equals("toolSelected")) {
            return;
        }

        ToolI newTool = checkTool();
        if (newTool == null) {
            currentTool = null;
            currentParams = null;
        }

        if (newTool != null && !newTool.equals(currentTool)) {
            currentTool = newTool;
            // fetch parameters
            currentParams = fetchParameters(newTool);
        }

        // panel is valid, if parameters have been determined; even empty
        // parameter list is ok
        isValid = currentTool != null && currentParams != null;

        if (oldState != isValid) {
            fireChangeEvent(this, oldState, isValid);
        }
    }

    private ToolI checkTool() {
        NotificationLineSupport nls = model.getNotificationLineSupport();
        nls.clearMessages();
        ToolI t = getComponent().getTool();
        ToolType tt = getComponent().getToolType();

        switch (tt) {
            case PROJECT:
                return t;
            case GLOBAL:
                if (t == null) {
                    return null;
                }
                for (ToolI pTool : projTools) {
                    if (pTool.getName().equals(t.getName())) {
                        nls.setErrorMessage("Tool with same name already exists in project.");
                        return null; // tool already present in project
                    }
                }
                return t;
            case USER_PROVIDED:
                if (t == null) {
                    return null;
                }

                String newVersion = getComponent().getNewToolVersion();
                if (newVersion == null || newVersion.isEmpty()) {
                    nls.setErrorMessage("Missing version.");
                    return null;
                } else {
                    try {
                        float parseFloat = Float.parseFloat(newVersion);
                        t.setVersion(parseFloat);
                    } catch (NumberFormatException nfe) {
                        nls.setErrorMessage("Invalid version, needs to be numeric (e.g. 1.1)");
                        return null;
                    }
                }

                for (ToolI pTool : projTools) {
                    if (pTool.getName().equals(t.getName())) {
                        nls.setErrorMessage("Tool with same name already exists in project.");
                        return null; // tool already present in project
                    }
                }

                String xmlData = t.getXML();
                // TODO: validate content

                return t;
            default:
                assert false;
                return null;
        }
    }

    private List<JobParameterI> fetchParameters(ToolI t) {
        if (t == null) {
            return null;
        }

        ParameterRetriever pr = new ParameterRetriever(t.getMaster(), t, getComponent().getToolType());
        pr.execute();
        try {
            Collection<JobParameterI> params = pr.get();
            // we need a list instead of a collection, convert..
            return new ArrayList<>(params);
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
