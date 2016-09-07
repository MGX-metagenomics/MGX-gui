package de.cebitec.mgx.gui.goldstandard.wizards.addgoldstandard;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.openide.WizardDescriptor;

/**
 *
 * @author sjaenick
 */
public class AddGoldstandardWizardDescriptor extends WizardDescriptor {

    private final AddGoldstandardWizardPanel1 p1 = new AddGoldstandardWizardPanel1();        

    public AddGoldstandardWizardDescriptor() {
        List<Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(p1);        
        this.setPanelsAndSettings(new ArrayIterator<>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Gold standard wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);        
    }

    public File getGoldstandardFile() {
        return (File) getProperty(AddGoldstandardVisualPanel1.PROP_MGSFILE);
    }

}
