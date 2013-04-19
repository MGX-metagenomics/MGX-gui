package de.cebitec.mgx.gui.wizard.extract;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.TermAccess;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.datamodel.Term;
import de.cebitec.mgx.gui.wizard.seqrun.SeqRunWizardDescriptor;
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
    private DNAExtract extract = null;

    public static final String INVOCATION_MODE = "invocationMode";
    public static final String CREATE_MODE = "CREATE";
    public static final String EDIT_MODE = "EDIT";
    
    
    public DNAExtractWizardDescriptor() {
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

    public DNAExtractWizardDescriptor(DNAExtract d) {
        this();
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
        final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
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
    

    public DNAExtract getDNAExtract() {
        if (extract == null) {
            extract = new DNAExtract();
        }

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
