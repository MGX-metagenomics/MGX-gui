package de.cebitec.mgx.gui.wizard.seqrun;

import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.openide.WizardDescriptor;

/**
 *
 * @author sjaenick
 */
public class SeqRunWizardDescriptor extends WizardDescriptor {

    private SeqRunWizardPanel1 p1 = new SeqRunWizardPanel1();
    private SeqRunWizardPanel2 p2 = new SeqRunWizardPanel2();
    private SeqRun seqrun = null;

    public SeqRunWizardDescriptor() {
        List<Panel<WizardDescriptor>> panels = new ArrayList<Panel<WizardDescriptor>>();
        panels.add(p1);
        panels.add(p2);
        this.setPanelsAndSettings(new ArrayIterator<WizardDescriptor>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Sequencing run wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName(), p2.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
    }

    public SeqRunWizardDescriptor(SeqRun d) {
        List<Panel<WizardDescriptor>> panels = new ArrayList<Panel<WizardDescriptor>>();
        panels.add(p1);
        this.setPanelsAndSettings(new ArrayIterator<WizardDescriptor>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Sequencing run wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
        this.seqrun = d;
        putProperty(SeqRunVisualPanel1.PROP_METHOD, d.getSequencingMethod());
        putProperty(SeqRunVisualPanel1.PROP_PLATFORM, d.getSequencingTechnology());
        putProperty(SeqRunVisualPanel1.PROP_SUBMITTED, d.getSubmittedToINSDC());
        putProperty(SeqRunVisualPanel1.PROP_ACCESSION, d.getAccession());
        p1.setProperties(this);
    }

    public SeqRun getSeqRun() {
        if (seqrun == null) {
            seqrun = new SeqRun();
        }

        seqrun.setSequencingMethod((String) getProperty(SeqRunVisualPanel1.PROP_METHOD))
                .setSequencingTechnology((String) getProperty(SeqRunVisualPanel1.PROP_PLATFORM))
                .setSubmittedToINSDC((Boolean) getProperty(SeqRunVisualPanel1.PROP_SUBMITTED));

        if (seqrun.getSubmittedToINSDC()) {
            seqrun.setAccession((String) getProperty(SeqRunVisualPanel1.PROP_ACCESSION));
        }
        return seqrun;
    }

    public File getSequenceFile() {
        return (File) getProperty(SeqRunVisualPanel2.PROP_SEQFILE);
    }
}
