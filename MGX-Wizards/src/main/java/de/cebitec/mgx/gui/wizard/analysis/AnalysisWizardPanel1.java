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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.openide.NotificationLineSupport;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class AnalysisWizardPanel1 implements WizardDescriptor.AsynchronousValidatingPanel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the component from this class, just use getComponent().
     */
    private AnalysisVisualPanel1 component;
    private WizardDescriptor model = null;
    private boolean isValid = false;
    private final EventListenerList listeners = new EventListenerList();
    //
    private List<Tool> projTools;
    private List<Tool> serverTools;
    private MGXMaster master = null;

    public void setMaster(MGXMaster master) {
        this.master = master;
        ToolRetriever tr = new ToolRetriever(master) {
            @Override
            protected void done() {
                try {
                    Pair<List<Tool>, List<Tool>> ret = get();
                    projTools = ret.getSecond();
                    serverTools = ret.getFirst();
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

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public AnalysisVisualPanel1 getComponent() {
        if (component == null) {
            component = new AnalysisVisualPanel1();
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
        model = settings;
        AnalysisVisualPanel1 c = getComponent();
        c.addPropertyChangeListener(this);
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        model = settings;
        AnalysisVisualPanel1 c = getComponent();
        Tool tool = c.getTool();
        ToolType ttype = c.getToolType();
        model.putProperty(AnalysisWizardIterator.PROP_TOOL, tool);
        model.putProperty(AnalysisWizardIterator.PROP_TOOLTYPE, ttype);

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
        NotificationLineSupport nls = model.getNotificationLineSupport();
        nls.setErrorMessage("");
        Tool t = getComponent().getTool();
        ToolType tt = getComponent().getToolType();

        switch (tt) {
            case PROJECT:
                return t != null;
            case GLOBAL:
                if (t == null) {
                    return false;
                }
                for (Tool pTool : projTools) {
                    if (pTool.getName().equals(t.getName())) {
                        nls.setErrorMessage("Tool with same name already exists in project.");
                        return false; // tool already present in project
                    }
                }
                return true;
            case USER_PROVIDED:
                if (t == null) {
                    return false;
                }

                String newVersion = getComponent().getNewToolVersion();
                if (newVersion == null || newVersion.isEmpty()) {
                    nls.setErrorMessage("Missing version.");
                    return false;
                } else {
                    try {
                        float parseFloat = Float.parseFloat(newVersion);
                        t.setVersion(parseFloat);
                    } catch (NumberFormatException nfe) {
                        nls.setErrorMessage("Invalid version, needs to be numeric (e.g. 1.1)");
                        return false;
                    }
                }

                for (Tool pTool : projTools) {
                    if (pTool.getName().equals(t.getName())) {
                        nls.setErrorMessage("Tool with same name already exists in project.");
                        return false; // tool already present in project
                    }
                }

                return true;
            default:
                assert false;
                return false;
        }
    }
    private CountDownLatch latch = null;

    @Override
    public void prepareValidation() {

        if (isValid) {
            latch = new CountDownLatch(1);
            ParameterRetriever pr = new ParameterRetriever(master, getComponent().getTool(), getComponent().getToolType()) {
                @Override
                protected void done() {
                    try {
                        Collection<JobParameter> params = get();
                        // we need a list instead of a collection, convert..
                        List<JobParameter> list =  new ArrayList<>(params);
                        model.putProperty(AnalysisWizardIterator.PROP_PARAMETERS, list);
                    } catch (InterruptedException | ExecutionException ex) {
                        model.putProperty(AnalysisWizardIterator.PROP_PARAMETERS, null);
                        Exceptions.printStackTrace(ex);
                    }
                    latch.countDown();
                    super.done();
                }
            };
            pr.execute();
        }
    }

    @Override
    public void validate() throws WizardValidationException {
        if (latch != null) {
            try {
                latch.await();
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
                model.putProperty(AnalysisWizardIterator.PROP_PARAMETERS, null);
            }
        }
    }
}
