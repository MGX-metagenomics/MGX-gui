package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.goldstandard.actions.AddGoldstandard;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class SelectSingleJobWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, ListSelectionListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SelectSingleJobVisualPanel1 component;
    private List<JobI> jobs;
    private SeqRunI seqrun;

    public SelectSingleJobWizardPanel1(SeqRunI seqrun) throws MGXException {
        this.seqrun = seqrun;
        List<JobI> allJobs = seqrun.getMaster().Job().BySeqRun(seqrun);
        jobs = new ArrayList<>(allJobs.size());
        for (JobI job : allJobs){
            if (job.getStatus() == JobState.FINISHED){
                job.setTool(seqrun.getMaster().Tool().ByJob(job));
                if (!job.getTool().getName().equals(AddGoldstandard.TOOL_NAME))
                    jobs.add(job);
            }
        }
    }    
    
    
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public SelectSingleJobVisualPanel1 getComponent() {
        if (component == null) {
            component = new SelectSingleJobVisualPanel1(jobs);
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
    
    public String getName(){
        return component.getName();
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
        if (! e.getValueIsAdjusting()){
            try {
                JobI job = component.getSelectedJob();
                Iterator<AttributeI> it = job.getMaster().Attribute().ByJob(job);
                Set<AttributeTypeI> set = new HashSet<>();
                while (it.hasNext()){
                    AttributeI attr = it.next();
                    set.add(attr.getAttributeType());
                }
                component.setAttributeTypeList(set);
                component.enableAttributeTypeBox(!set.isEmpty());                
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
