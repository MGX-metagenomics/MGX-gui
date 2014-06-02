package de.cebitec.mgx.gui.wizard.seqrun;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.controller.TermAccess;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.Term;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class SeqRunWizardDescriptor extends WizardDescriptor {

    private SeqRunWizardPanel1 p1 = new SeqRunWizardPanel1();
    private SeqRunWizardPanel2 p2 = new SeqRunWizardPanel2();
    private SeqRunI seqrun = null;
    
    public static final String INVOCATION_MODE = "invocationMode";
    public static final String CREATE_MODE = "CREATE";
    public static final String EDIT_MODE = "EDIT";

    public SeqRunWizardDescriptor() {
        List<Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(p1);
        panels.add(p2);
        this.setPanelsAndSettings(new ArrayIterator<>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Sequencing run wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName(), p2.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);   
        putProperty(SeqRunWizardDescriptor.INVOCATION_MODE, CREATE_MODE);     
        setData();
    }

    public SeqRunWizardDescriptor(SeqRunI d) {
        List<Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(p1);

        setData();

        this.setPanelsAndSettings(new ArrayIterator<>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("Sequencing run wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
        this.seqrun = d;
        putProperty(SeqRunVisualPanel1.PROP_NAME, d.getName());
        putProperty(SeqRunVisualPanel1.PROP_METHOD, d.getSequencingMethod());
        putProperty(SeqRunVisualPanel1.PROP_PLATFORM, d.getSequencingTechnology());
        putProperty(SeqRunVisualPanel1.PROP_SUBMITTED, d.getSubmittedToINSDC());
        putProperty(SeqRunVisualPanel1.PROP_ACCESSION, d.getAccession());
        putProperty(SeqRunWizardDescriptor.INVOCATION_MODE, EDIT_MODE);  
        p1.setProperties(this);
    }

    private void setData() {
        final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                p1.setMethods(m.Term().byCategory(TermAccess.SEQ_METHODS).toArray(new Term[]{}));
                p1.setPlatforms(m.Term().byCategory(TermAccess.SEQ_PLATFORMS).toArray(new Term[]{}));
                p1.setSeqRuns(m.SeqRun().fetchall());
                return null;
            }
        };
        sw.execute();
        try {
            sw.get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public SeqRunI getSeqRun(MGXMasterI m) {
        if (seqrun == null) {
            seqrun = new SeqRun(m);
        }

        seqrun.setSequencingMethod((Term) getProperty(SeqRunVisualPanel1.PROP_METHOD))
                .setSequencingTechnology((Term) getProperty(SeqRunVisualPanel1.PROP_PLATFORM))
                .setSubmittedToINSDC((Boolean) getProperty(SeqRunVisualPanel1.PROP_SUBMITTED))
                .setName((String) getProperty(SeqRunVisualPanel1.PROP_NAME));

        if (seqrun.getSubmittedToINSDC()) {
            seqrun.setAccession((String) getProperty(SeqRunVisualPanel1.PROP_ACCESSION));
        }
        return seqrun;
    }

    public File getSequenceFile() {
        return (File) getProperty(SeqRunVisualPanel2.PROP_SEQFILE);
    }
}
