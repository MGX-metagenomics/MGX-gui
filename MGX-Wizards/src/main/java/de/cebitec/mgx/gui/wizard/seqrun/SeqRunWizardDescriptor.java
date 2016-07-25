package de.cebitec.mgx.gui.wizard.seqrun;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.TermAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.TermI;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
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

    private final SeqRunWizardPanel1 p1 = new SeqRunWizardPanel1();
    private final SeqRunWizardPanel2 p2 = new SeqRunWizardPanel2();
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
        
        // in 'edit mode', do not run default tools
        putProperty(SeqRunVisualPanel1.PROP_RUNTOOLS, false);
        
        putProperty(SeqRunWizardDescriptor.INVOCATION_MODE, EDIT_MODE);
        p1.setProperties(this);
    }

    private void setData() {
        final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws MGXException {
                List<TermI> methods = m.Term().byCategory(TermAccessI.SEQ_METHODS);
                List<TermI> platforms = m.Term().byCategory(TermAccessI.SEQ_PLATFORMS);
                Collections.<TermI>sort(methods);
                Collections.<TermI>sort(platforms);
                p1.setMethods(methods.toArray(new TermI[]{}));
                p1.setPlatforms(platforms.toArray(new TermI[]{}));
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

    public String getSeqRunName() {
        return (String) getProperty(SeqRunVisualPanel1.PROP_NAME);
    }

    public TermI getSequencingMethod() {
        return (TermI) getProperty(SeqRunVisualPanel1.PROP_METHOD);
    }

    public TermI getSequencingTechnology() {
        return (TermI) getProperty(SeqRunVisualPanel1.PROP_PLATFORM);
    }

    public Boolean getSubmittedToINSDC() {
        return (Boolean) getProperty(SeqRunVisualPanel1.PROP_SUBMITTED);
    }

    public String getAccession() {
        return (String) getProperty(SeqRunVisualPanel1.PROP_ACCESSION);
    }

    public SeqRunI getSeqRun(MGXMasterI m) {
        // only to be used when editing an existing instance

        seqrun.setSequencingMethod((TermI) getProperty(SeqRunVisualPanel1.PROP_METHOD))
                .setSequencingTechnology((TermI) getProperty(SeqRunVisualPanel1.PROP_PLATFORM))
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

    public boolean runDefaultTools() {
        if (getProperty(SeqRunWizardDescriptor.INVOCATION_MODE).equals(EDIT_MODE)) {
            return false;
        }
        return (boolean) getProperty(SeqRunVisualPanel1.PROP_RUNTOOLS);
    }
}
