package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.openide.WizardDescriptor;

/**
 *
 * @author sjaenick
 */
public class TimeEvalJobWizardDescriptor extends WizardDescriptor {

    private final TimeEvalJobWizardPanel1 p1;
    protected final SeqRunI seqrun;

    public TimeEvalJobWizardDescriptor(SeqRunI seqrun) throws MGXException {
        super();
        this.seqrun = seqrun;
        p1 = new TimeEvalJobWizardPanel1(seqrun);
        List<Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(p1);
        this.setPanelsAndSettings(new ArrayIterator<>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Select job wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
    }

    @SuppressWarnings("unchecked")
    public List<JobI> getJobs() {
        return (List<JobI>) getProperty(SelectJobsVisualPanel1.PROP_JOBS);
    }

}
