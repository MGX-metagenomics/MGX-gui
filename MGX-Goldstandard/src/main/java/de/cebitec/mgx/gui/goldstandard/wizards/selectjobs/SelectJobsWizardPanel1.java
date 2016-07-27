package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.api.model.SeqRunI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
    private final String atLabel;

    private boolean isValid = false;

    public SelectJobsWizardPanel1(SeqRunI seqrun, boolean hierarchicAT, int maxSelected) throws MGXException {
        this.seqrun = seqrun;
        this.maxSelected = maxSelected;
        
        if (hierarchicAT){
            atLabel = "Select attribute type from wanted tree:";
        } else {
            atLabel = "Select wanted attribute type:";
        }
        
        List<JobI> allJobs = seqrun.getMaster().Job().BySeqRun(seqrun);
        jobs = new HashMap<>(allJobs.size());
        for (JobI job : allJobs) {
            job.setTool(seqrun.getMaster().Tool().ByJob(job));
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
    public SelectJobsVisualPanel1 getComponent() {
        if (component == null) {
            component = new SelectJobsVisualPanel1(jobs, atLabel);
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
        model.putProperty(SelectJobsVisualPanel1.PROP_JOBS, getComponent().getSelectedJobs());
        model.putProperty(SelectJobsVisualPanel1.PROP_ATTRIBUTETYPE, getComponent().getSelectedAttributeType());
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
//        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (!e.getValueIsAdjusting()) {
            try {
                List<JobI> jobList = component.getSelectedJobs();
                Set<AttributeTypeI> sharedATs = null;
                for (JobI job : jobList) {
                    Collection<AttributeTypeI> atList = jobs.get(job);
                    if (atList == null) {

                        Iterator<AttributeTypeI> it = seqrun.getMaster().AttributeType().byJob(job);
                        atList = new LinkedList<>();
                        while (it.hasNext()) {
                            AttributeTypeI at = it.next();
                            atList.add(at);
                        }
                        jobs.put(job, atList);
                    }
                    if (sharedATs == null) {
                        sharedATs = new HashSet<>();
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
        return component.getAttributeTypeListCount() != 0 && component.getSelectedJobs().size() < maxSelected;
    }

}