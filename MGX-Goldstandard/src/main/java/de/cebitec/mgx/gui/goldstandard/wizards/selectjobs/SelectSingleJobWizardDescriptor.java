package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.goldstandard.wizards.addgoldstandard.AddGoldstandardVisualPanel1;
import de.cebitec.mgx.gui.goldstandard.wizards.addgoldstandard.AddGoldstandardWizardPanel1;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.openide.WizardDescriptor;

/**
 *
 * @author sjaenick
 */
public class SelectSingleJobWizardDescriptor extends WizardDescriptor {

    //private final SelectSingleJobWizardPanel1 p1 = new SelectSingleJobWizardPanel1();
    protected final SeqRunI seqrun;

    public SelectSingleJobWizardDescriptor(SeqRunI seqrun) {
        this.seqrun = seqrun;
        List<Panel<WizardDescriptor>> panels = new ArrayList<>();
        //panels.add(p1);        
        this.setPanelsAndSettings(new ArrayIterator<>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Select job wizard");
        //putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);        
    }

}
