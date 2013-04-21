package de.cebitec.mgx.gui.wizard.analysis;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.JobParameter;
import de.cebitec.mgx.gui.datamodel.Tool;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.misc.ToolType;
import de.cebitec.mgx.gui.wizard.analysis.workers.ParameterRetriever;
import de.cebitec.mgx.gui.wizard.analysis.workers.ToolRetriever;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
    private AnalysisVisualPanel1 component;
    private WizardDescriptor model = null;
    private boolean isValid = false;
    private final EventListenerList listeners = new EventListenerList();
    //
    private List<Tool> projTools;
    private List<Tool> serverTools;
    private MGXMaster master = null;
    private Tool currentTool = null;
    private List<JobParameter> currentParams = null;

    public void setWizardDescriptor(WizardDescriptor wdesc) {
        model = wdesc;
    }

    public void setMaster(MGXMaster master) {
        this.master = master;
        ToolRetriever tr = new ToolRetriever(master) {
            @Override
            protected void done() {
                try {
                    Pair<Iterator<Tool>, Iterator<Tool>> ret = get();
                    projTools = new ArrayList<>();
                    Iterator<Tool> pIter = ret.getSecond();
                    while (pIter.hasNext()) {
                        projTools.add(pIter.next());
                    }
                    serverTools = new ArrayList<>();
                    Iterator<Tool> sIter = ret.getFirst();
                    while (pIter.hasNext()) {
                        serverTools.add(sIter.next());
                    }
                    getComponent().setProjectTools(projTools);
                    getComponent().setServerTools(serverTools);
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
                super.done();
            }
        };
        tr.execute();
    }

    public String getName() {
        return "Select tool";
    }

    @Override
    public AnalysisVisualPanel1 getComponent() {
        if (component == null) {
            component = new AnalysisVisualPanel1();
            component.addPropertyChangeListener(this);
        }
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

        Tool newTool = checkTool();
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

    private Tool checkTool() {
        NotificationLineSupport nls = model.getNotificationLineSupport();
        nls.clearMessages();
        Tool t = getComponent().getTool();
        ToolType tt = getComponent().getToolType();

        switch (tt) {
            case PROJECT:
                return t;
            case GLOBAL:
                if (t == null) {
                    return null;
                }
                for (Tool pTool : projTools) {
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

                for (Tool pTool : projTools) {
                    if (pTool.getName().equals(t.getName())) {
                        nls.setErrorMessage("Tool with same name already exists in project.");
                        return null; // tool already present in project
                    }
                }
                
                String xmlData = t.getXMLFile();
                // TODO: validate content

                return t;
            default:
                assert false;
                return null;
        }
    }

    private List<JobParameter> fetchParameters(Tool t) {
        if (t == null) {
            return null;
        }

        ParameterRetriever pr = new ParameterRetriever(master, t, getComponent().getToolType());
        pr.execute();
        try {
            Collection<JobParameter> params = pr.get();
            // we need a list instead of a collection, convert..
            return new ArrayList<>(params);
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
