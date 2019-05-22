package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.common.JobState;
import java.util.ArrayList;
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
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class SelectJobsWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, ListSelectionListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SelectJobsVisualPanel1 component;
    private WizardDescriptor model;
    private final EventListenerList listeners = new EventListenerList();

    private final Map<JobI, Collection<AttributeTypeI>> jobs;
    private final SeqRunI seqrun;
    private final int maxSelected;
    private final boolean exactlySelected;
//    private final String atLabel;
    private final String selectJobsLabel;

    private boolean isValid = false;

    public SelectJobsWizardPanel1(SeqRunI seqrun, boolean hierarchicAT, int maxSelected, boolean exactlySelected) throws MGXException {
        this.seqrun = seqrun;
        this.maxSelected = maxSelected;
        this.exactlySelected = exactlySelected;

        if (maxSelected == 1) {
            selectJobsLabel = "Select one job:";
        } else if (exactlySelected) {
            selectJobsLabel = "Select exactly " + maxSelected + " jobs for comparison:";
        } else if (maxSelected == Integer.MAX_VALUE) {
            selectJobsLabel = "Select jobs for comparison:";
        } else {
            selectJobsLabel = "Select up to " + maxSelected + " jobs for comparison:";
        }

        List<JobI> allJobs = seqrun.getMaster().Job().BySeqRun(seqrun);
        jobs = new HashMap<>(allJobs.size());
        for (JobI job : allJobs) {
            if (job.getStatus() == JobState.FINISHED) {
                seqrun.getMaster().Tool().ByJob(job);
                jobs.put(job, null);
            }
        }
    }

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public SelectJobsVisualPanel1 getComponent() {
        if (component == null) {
            component = new SelectJobsVisualPanel1(jobs.keySet(), selectJobsLabel);
            component.addListSelectionListener(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    public String getName() {
        return getComponent().getName();
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
        model.putProperty(SelectJobsVisualPanel1.PROP_ATTRIBUTETYPE, getComponent().getSelectedAttributeType());
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            try {
                List<JobI> jobList = component.getSelectedJobs();
                List<AttributeTypeI> sharedATs = null;
                for (JobI job : jobList) {
                    Collection<AttributeTypeI> atList = jobs.get(job);
                    if (atList == null) {

                        Iterator<AttributeTypeI> it = seqrun.getMaster().AttributeType().byJob(job);
                        atList = new ArrayList<>();
                        while (it.hasNext()) {
                            AttributeTypeI next = it.next();
                            if (!atList.contains(next)) {
                                atList.add(next);
                            }
                        }
                        jobs.put(job, atList);
                    }
                    if (sharedATs == null) {
                        sharedATs = new ArrayList<>();
                        sharedATs.addAll(atList);
                    } else {
                        sharedATs.retainAll(atList);
                    }
                }

                component.setAttributeTypeList(sharedATs);
                component.enableAttributeTypeBox(sharedATs != null && !sharedATs.isEmpty());

                boolean oldState = isValid;
                isValid = checkValidity();
                fireChangeEvent(this, oldState, isValid);
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private boolean checkValidity() {
        if (component.getSelectedJobs().size() < 2) {
            return false;
        }
        if (exactlySelected) {
            return component.getAttributeTypeListCount() != 0 && component.getSelectedJobs().size() == maxSelected;
        } else {
            return component.getAttributeTypeListCount() != 0 && component.getSelectedJobs().size() <= maxSelected;
        }
    }

}
