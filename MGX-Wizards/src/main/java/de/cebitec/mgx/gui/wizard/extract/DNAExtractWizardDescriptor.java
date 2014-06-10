package de.cebitec.mgx.gui.wizard.extract;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.DNAExtractI;
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
public class DNAExtractWizardDescriptor extends WizardDescriptor {

    private DNAExtractWizardPanel1 p1 = new DNAExtractWizardPanel1();
    private DNAExtractWizardPanel2 p2 = new DNAExtractWizardPanel2();
    private DNAExtractI extract = null;
    private final MGXMasterI master;

    public static final String INVOCATION_MODE = "invocationMode";
    public static final String CREATE_MODE = "CREATE";
    public static final String EDIT_MODE = "EDIT";

    public DNAExtractWizardDescriptor(MGXMasterI master) {
        this.master = master;
        List<Panel<WizardDescriptor>> panels = new ArrayList<>();
        panels.add(p1);
        panels.add(p2);
        this.setPanelsAndSettings(new ArrayIterator<>(panels), this);
        this.setTitleFormat(new MessageFormat("{0}"));
        this.setTitle("DNA extract wizard");
        putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{p1.getName(), p2.getName()});
        putProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
        putProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
        putProperty(DNAExtractWizardDescriptor.INVOCATION_MODE, CREATE_MODE);
        setData();
    }

    public DNAExtractWizardDescriptor(MGXMasterI master, DNAExtractI d) {
        this(master);
        this.extract = d;
        putProperty(DNAExtractVisualPanel1.PROP_NAME, d.getName());
        putProperty(DNAExtractVisualPanel1.PROP_METHOD, d.getMethod());
        putProperty(DNAExtractVisualPanel1.PROP_PROTOCOL, d.getProtocol());
        putProperty(DNAExtractVisualPanel1.PROP_FIVEPRIMER, d.getFivePrimer());
        putProperty(DNAExtractVisualPanel1.PROP_THREEPRIMER, d.getThreePrimer());
        putProperty(DNAExtractVisualPanel1.PROP_GENE, d.getTargetGene());
        putProperty(DNAExtractVisualPanel1.PROP_FRAGMENT, d.getTargetFragment());
        putProperty(DNAExtractVisualPanel2.PROP_DESCRIPTION, d.getDescription());
        putProperty(DNAExtractWizardDescriptor.INVOCATION_MODE, EDIT_MODE);

        p1.setProperties(this);
        p2.setProperties(this);

    }

    private void setData() {
        final MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                p1.setDNAExtracts(m.DNAExtract().fetchall());
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

    public String getExtractName() {
        return (String) getProperty(DNAExtractVisualPanel1.PROP_NAME);
    }

    public String getMethod() {
        return (String) getProperty(DNAExtractVisualPanel1.PROP_METHOD);
    }

    public String getProtocol() {
        return (String) getProperty(DNAExtractVisualPanel1.PROP_PROTOCOL);
    }

    public String getFivePrimer() {
        return (String) getProperty(DNAExtractVisualPanel1.PROP_FIVEPRIMER);
    }

    public String getThreePrimer() {
        return (String) getProperty(DNAExtractVisualPanel1.PROP_THREEPRIMER);
    }

    public String getTargetGene() {
        return (String) getProperty(DNAExtractVisualPanel1.PROP_GENE);
    }

    public String getTargetFragment() {
        return (String) getProperty(DNAExtractVisualPanel1.PROP_FRAGMENT);
    }

    public String getDescription() {
        return (String) getProperty(DNAExtractVisualPanel2.PROP_DESCRIPTION);
    }

    public DNAExtractI getDNAExtract() {
        // only use this for editing an instance 
        extract.setName((String) getProperty(DNAExtractVisualPanel1.PROP_NAME))
                .setMethod((String) getProperty(DNAExtractVisualPanel1.PROP_METHOD))
                .setProtocol((String) getProperty(DNAExtractVisualPanel1.PROP_PROTOCOL))
                .setFivePrimer((String) getProperty(DNAExtractVisualPanel1.PROP_FIVEPRIMER))
                .setThreePrimer((String) getProperty(DNAExtractVisualPanel1.PROP_THREEPRIMER))
                .setTargetGene((String) getProperty(DNAExtractVisualPanel1.PROP_GENE))
                .setTargetFragment((String) getProperty(DNAExtractVisualPanel1.PROP_FRAGMENT))
                .setDescription((String) getProperty(DNAExtractVisualPanel2.PROP_DESCRIPTION));
        return extract;
    }
}
