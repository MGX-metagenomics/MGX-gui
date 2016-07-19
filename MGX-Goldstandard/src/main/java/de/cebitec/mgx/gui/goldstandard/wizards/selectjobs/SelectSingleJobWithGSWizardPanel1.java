package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.goldstandard.actions.AddGoldstandard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JComboBox;
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

    private boolean isValid = false;

    public SelectSingleJobWithGSWizardPanel1(SeqRunI seqrun) throws MGXException {
        this.seqrun = seqrun;
        List<JobI> allJobs = seqrun.getMaster().Job().BySeqRun(seqrun);
        jobs = new HashMap<>(allJobs.size());
//        for (JobI job : allJobs) {
//            job.setTool(seqrun.getMaster().Tool().ByJob(job));
//            if (job.getTool().getName().equals(AddGoldstandard.TOOL_NAME)) {
//                goldstandard = job;
//                gsAttributeTypes = new HashSet<>();
//                Iterator<AttributeTypeI> it = seqrun.getMaster().AttributeType().byJob(goldstandard);
//                while (it.hasNext()) {
//                    gsAttributeTypes.add(it.next());
//                }
//            }
//        }
//        allJobs.remove(goldstandard);
        for (JobI job : allJobs) {
            if (job.getStatus() == JobState.FINISHED) {
                job.setTool(seqrun.getMaster().Tool().ByJob(job));
                jobs.put(job, null);
            }
        }
    }

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public SelectSingleJobWithGSVisualPanel1 getComponent() {
        if (component == null) {
            component = new SelectSingleJobWithGSVisualPanel1(jobs);
            component.addListSelectionListener(this);
            component.addGSComboBoxSelectionListener(this);
            updateJobList();
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
        // If it is always OK to press Next or Finish, then:
        return isValid;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
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

            for (ChangeListener listener
                    : listeners.getListeners(ChangeListener.class)) {
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
        model.putProperty(SelectSingleJobWithGSVisualPanel1.PROP_JOB, getComponent().getSelectedJob());
        model.putProperty(SelectSingleJobWithGSVisualPanel1.PROP_ATTRIBUTETYPE, getComponent().getSelectedAttributeType());
        model.putProperty(SelectSingleJobWithGSVisualPanel1.PROP_GOLDSTANDARD, goldstandard);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
//        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (!e.getValueIsAdjusting()) {
            JobI job = component.getSelectedJob();
            if (job == null){
                component.deactivateAttributeTypeList();
                return;
            }
            Collection<AttributeTypeI> atList = jobs.get(job);
            if (atList == null) {
                try {
                    Iterator<AttributeTypeI> it = seqrun.getMaster().AttributeType().byJob(job);
                    atList = new LinkedList<>();
                    while (it.hasNext()) {
                        AttributeTypeI at = it.next();
                        atList.add(at);
                    }
                    jobs.put(job, atList);
                } catch (MGXException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            Collection<AttributeTypeI> filteredATList = new ArrayList<>(atList.size());
            for (AttributeTypeI at : atList) {
                if (jobs.get(goldstandard).contains(at)) {
                    filteredATList.add(at);
                }
            }
            component.setAttributeTypeList(filteredATList);
            component.enableAttributeTypeBox(atList != null && !atList.isEmpty());

            boolean oldState = isValid;
            isValid = checkValidity();
            fireChangeEvent(this, oldState, isValid);
        }
    }

    private boolean checkValidity() {
        return component.getAttributeTypeListCount() != 0;
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
            Collection<JobI> jobCol = new LinkedList<>();
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
        List<AttributeTypeI> atList = new LinkedList<>();
        while (it.hasNext()) {
            AttributeTypeI at = it.next();
            atList.add(at);
        }
        return atList;
    }
}
