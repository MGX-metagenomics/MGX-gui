package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.common.JobState;
import de.cebitec.mgx.gui.goldstandard.actions.AddGoldstandard;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class TimeEvalJobWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, ListSelectionListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private TimeEvalJobVisualPanel1 component;
    private WizardDescriptor model;
    private final EventListenerList listeners = new EventListenerList();

    private final Map<JobI, Collection<AttributeTypeI>> jobs;
    private JobI goldstandard;
    private Set<AttributeTypeI> gsAttributeTypes;

    private boolean isValid = false;

    public TimeEvalJobWizardPanel1(SeqRunI seqrun) throws MGXException {
        List<JobI> allJobs = seqrun.getMaster().Job().BySeqRun(seqrun);
        jobs = new HashMap<>(allJobs.size());
        for (JobI job : allJobs) {
            // fetch tool
            seqrun.getMaster().Tool().ByJob(job);
            if (job.getTool().getName().equals(AddGoldstandard.TOOL_NAME)) {
                goldstandard = job;
                gsAttributeTypes = new HashSet<>();
                Iterator<AttributeTypeI> it = seqrun.getMaster().AttributeType().byJob(goldstandard);
                while (it.hasNext()) {
                    gsAttributeTypes.add(it.next());
                }
            }
        }
        allJobs.remove(goldstandard);
        for (JobI job : allJobs) {
            if (job.getStatus() == JobState.FINISHED) {
                jobs.put(job, null);
            }
        }
    }

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public TimeEvalJobVisualPanel1 getComponent() {
        if (component == null) {
            component = new TimeEvalJobVisualPanel1(jobs);
            component.addListSelectionListener(this);

        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    public String getName() {
        return component.getName();
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        listeners.add(ChangeListener.class, l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        listeners.remove(ChangeListener.class, l);
    }

    protected final void fireChangeEvent(Object source, boolean oldState, boolean newState) {
        if (oldState != newState) {
            ChangeEvent ev = new ChangeEvent(source);

            for (ChangeListener listener : listeners.getListeners(ChangeListener.class)) {
                listener.stateChanged(ev);
            }
        }
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.model = wiz;
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        model.putProperty(SelectJobsVisualPanel1.PROP_JOBS, getComponent().getSelectedJobs());
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            boolean oldState = isValid;
            isValid = checkValidity();
            fireChangeEvent(this, oldState, isValid);
        }
    }

    private boolean checkValidity() {
        return !component.getSelectedJobs().isEmpty();
    }

}
