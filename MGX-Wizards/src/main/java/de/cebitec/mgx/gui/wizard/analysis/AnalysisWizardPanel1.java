package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.ToolType;
import de.cebitec.mgx.api.model.Identifiable;
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
    private final MGXMasterI master;
    //
    private final List<ToolI> projTools;
    private String currentToolName = null;
    private List<JobParameterI> currentParams = null;

    public AnalysisWizardPanel1(MGXMasterI master, List<ToolI> projectTools, List<ToolI> serverTools) {
        this.projTools = projectTools;
        this.master = master;
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

        model.putProperty(AnalysisWizardIterator.PROP_TOOLTYPE, c.getToolType());

        switch (c.getToolType()) {
            case PROJECT:
            case GLOBAL:
                model.putProperty(AnalysisWizardIterator.PROP_TOOLID, c.getToolId());
                model.putProperty(AnalysisWizardIterator.PROP_TOOLNAME, c.getToolName());
                break;
            case USER_PROVIDED:
                model.putProperty(AnalysisWizardIterator.PROP_TOOLNAME, c.getToolName());
                model.putProperty(AnalysisWizardIterator.PROP_TOOLDESC, c.getToolDescription());
                model.putProperty(AnalysisWizardIterator.PROP_TOOLAUTHOR, c.getToolAuthor());
                model.putProperty(AnalysisWizardIterator.PROP_TOOL_URL, c.getToolWebsite());
                model.putProperty(AnalysisWizardIterator.PROP_TOOL_DEFINITION, c.getToolDefinition());
                model.putProperty(AnalysisWizardIterator.PROP_TOOLVERSION, c.getToolVersion());
                break;
        }
        model.putProperty(AnalysisWizardIterator.PROP_PARAMETERS, currentParams);
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        boolean oldState = isValid;

        if (!evt.getPropertyName().equals("toolSelected")) {
            return;
        }

        boolean newToolIsValid = checkToolIsValid();
        if (!newToolIsValid) {
            currentToolName = null;
            currentParams = null;
        }
        String newToolName = getComponent().getToolName();

        if (newToolIsValid && !newToolName.equals(currentToolName)) {
            currentToolName = newToolName;
            // fetch parameters
            switch (getComponent().getToolType()) {
                case GLOBAL:
                case PROJECT:
                    currentParams = fetchParameters(getComponent().getToolId());
                    break;
                case USER_PROVIDED:
                    currentParams = fetchParameters(getComponent().getToolDefinition());
                    break;
            }
        }

        // panel is valid, if parameters have been determined; even empty
        // parameter list is ok
        isValid = currentToolName != null && currentParams != null;

        if (oldState != isValid) {
            fireChangeEvent(this, oldState, isValid);
        }
    }

    private boolean checkToolIsValid() {
        NotificationLineSupport nls = model.getNotificationLineSupport();
        nls.clearMessages();
        ToolType toolType = getComponent().getToolType();

        switch (toolType) {
            case PROJECT:
                return getComponent().getToolId() != Identifiable.INVALID_IDENTIFIER;
            case GLOBAL:
                if (getComponent().getToolId() == Identifiable.INVALID_IDENTIFIER) {
                    return false;
                }
                String toolName = getComponent().getToolName();
                for (ToolI pTool : projTools) {
                    if (pTool.getName().equals(toolName)) {
                        nls.setErrorMessage("Tool with same name already exists in project.");
                        return false; // tool already present in project
                    }
                }
                return true;
            case USER_PROVIDED:
                AnalysisVisualPanel1 c = getComponent();
                if (c.getToolName() == null || c.getToolDescription() == null || c.getToolAuthor() == null || c.getToolDefinition() == null) {
                    return false;
                }

                String newVersion = getComponent().getToolVersion();
                if (newVersion == null || newVersion.isEmpty()) {
                    nls.setErrorMessage("Missing version.");
                    return false;
                } else {
                    try {
                        Float.parseFloat(newVersion);
                    } catch (NumberFormatException nfe) {
                        nls.setErrorMessage("Invalid version, needs to be numeric (e.g. 1.1)");
                        return false;
                    }
                }

                for (ToolI pTool : projTools) {
                    if (pTool.getName().equals(getComponent().getToolName())) {
                        nls.setErrorMessage("Tool with same name already exists in project.");
                        return false; // tool already present in project
                    }
                }

                String xmlData = getComponent().getToolDefinition();
                // TODO: validate content

                return true;
            default:
                assert false;
                return false;
        }
    }

    private List<JobParameterI> fetchParameters(long toolId) {
        if (toolId == Identifiable.INVALID_IDENTIFIER) {
            return null;
        }

        ParameterRetriever pr = new ParameterRetriever(master, toolId, getComponent().getToolType());
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

    private List<JobParameterI> fetchParameters(String toolXml) {

        ParameterRetriever pr = new ParameterRetriever(master, toolXml);
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
