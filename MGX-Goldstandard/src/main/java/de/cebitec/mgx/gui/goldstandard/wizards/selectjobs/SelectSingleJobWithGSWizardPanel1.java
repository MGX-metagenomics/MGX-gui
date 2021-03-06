package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.common.JobState;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class SelectSingleJobWithGSWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, ListSelectionListener, ActionListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SelectSingleJobWithGSVisualPanel1 component;
    private WizardDescriptor model;
    private final EventListenerList listeners = new EventListenerList();

    private final Map<JobI, Collection<AttributeTypeI>> jobs;
    private JobI goldstandard;
    private final SeqRunI seqrun;
    private final int maxSelected;

    private boolean isValid = false;

    public SelectSingleJobWithGSWizardPanel1(SeqRunI seqrun, boolean hierarchicAT, int maxSelected) throws MGXException {
        this.seqrun = seqrun;
        this.maxSelected = maxSelected;

        List<JobI> allJobs = seqrun.getMaster().Job().BySeqRun(seqrun);
        jobs = new HashMap<>(allJobs.size());
        for (JobI job : allJobs) {
            if (job.getStatus() == JobState.FINISHED) {
                if (job.getTool() == null) {
                    // trigger seqrun fetch
                    seqrun.getMaster().Tool().ByJob(job);
                }
                jobs.put(job, null);
            }
        }
    }

    @Override
    public SelectSingleJobWithGSVisualPanel1 getComponent() {
        if (component == null) {
            component = new SelectSingleJobWithGSVisualPanel1(jobs);
            component.addListSelectionListener(this);
            component.addGSComboBoxSelectionListener(this);
            if (maxSelected == 1) {
                component.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            }
            updateJobList();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
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
        model.putProperty(SelectSingleJobWithGSVisualPanel1.PROP_JOB, getComponent().getSelectedJobs());
        model.putProperty(SelectSingleJobWithGSVisualPanel1.PROP_ATTRIBUTETYPE, getComponent().getSelectedAttributeType());
        model.putProperty(SelectSingleJobWithGSVisualPanel1.PROP_GOLDSTANDARD, goldstandard);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            try {
                List<JobI> jobList = component.getSelectedJobs();
                Set<AttributeTypeI> sharedATs = new HashSet<>(jobs.get(goldstandard));
                for (JobI job : jobList) {
                    Collection<AttributeTypeI> atList = jobs.get(job);
                    if (atList == null) {

                        Iterator<AttributeTypeI> it = seqrun.getMaster().AttributeType().byJob(job);
                        atList = new ArrayList<>();
                        while (it.hasNext()) {
                            atList.add(it.next());
                        }
                        jobs.put(job, atList);
                    }
                    sharedATs.retainAll(atList);

                }

                component.setAttributeTypeList(sharedATs);
                component.enableAttributeTypeBox(!sharedATs.isEmpty());

                boolean oldState = isValid;
                isValid = checkValidity();
                fireChangeEvent(this, oldState, isValid);
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private boolean checkValidity() {
        return component.getAttributeTypeListCount() != 0 && component.getSelectedJobs().size() <= maxSelected;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateJobList();
    }

    private void updateJobList() {
        try {
            goldstandard = component.getGoldstandard();
            Collection<AttributeTypeI> atCol;
            if (jobs.get(goldstandard) == null) {
                atCol = getAttributeTypes(goldstandard);
                jobs.put(goldstandard, atCol);
            }
            Collection<JobI> jobCol = new ArrayList<>();
            for (JobI job : jobs.keySet()) {
                if (!job.equals(goldstandard)) {
                    jobCol.add(job);
                }
            }
            component.setJobList(jobCol);
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private Collection<AttributeTypeI> getAttributeTypes(JobI job) throws MGXException {
        Iterator<AttributeTypeI> it = seqrun.getMaster().AttributeType().byJob(job);
        List<AttributeTypeI> atList = new ArrayList<>();
        while (it.hasNext()) {
            atList.add(it.next());
        }
        return atList;
    }
}
