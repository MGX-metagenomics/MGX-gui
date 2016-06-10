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
public class SelectSingleJobWizardDescriptor extends WizardDescriptor implements ChangeListener{

    private final SelectSingleJobWizardPanel1 p1;
    protected final SeqRunI seqrun;

    public SelectSingleJobWizardDescriptor(SeqRunI seqrun) throws MGXException {
        super();
        this.seqrun = seqrun;
        p1 = new SelectSingleJobWizardPanel1(seqrun);
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
    
    public JobI getJob(){
        return (JobI)getProperty(SelectSingleJobVisualPanel1.PROP_JOB);
    }
    
    public AttributeTypeI getAttributeType(){
        return (AttributeTypeI) getProperty(SelectSingleJobVisualPanel1.PROP_ATTRIBUTETYPE);
    }
    
    public JobI getGoldstandard(){
        return (JobI) getProperty(SelectSingleJobVisualPanel1.PROP_GOLDSTANDARD);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        return;
    }

}
