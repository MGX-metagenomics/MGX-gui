package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;

/**
 *
 * @author sjaenick
 */
public class SelectJobsWizardDescriptor extends WizardDescriptor implements ChangeListener {

    private final SelectJobsWizardPanel1 p1;
    protected final SeqRunI seqrun;

    public SelectJobsWizardDescriptor(SeqRunI seqrun, boolean hierarchicAT) throws MGXException {
        this(seqrun, hierarchicAT, Integer.MAX_VALUE, false);
    }

    public SelectJobsWizardDescriptor(SeqRunI seqrun, boolean hierarchicAT, int maxSelected) throws MGXException {
        this(seqrun, hierarchicAT, maxSelected, false);
    }

    public SelectJobsWizardDescriptor(SeqRunI seqrun, boolean hierarchicAT, int maxSelected, boolean exactlySelected) throws MGXException {
        super();
        this.seqrun = seqrun;
        p1 = new SelectJobsWizardPanel1(seqrun, hierarchicAT, maxSelected, exactlySelected);
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

    public AttributeTypeI getAttributeType() {
        return (AttributeTypeI) getProperty(SelectJobsVisualPanel1.PROP_ATTRIBUTETYPE);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        return;
    }

}
