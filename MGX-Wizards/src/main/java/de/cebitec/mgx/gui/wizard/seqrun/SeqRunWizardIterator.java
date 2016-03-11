package de.cebitec.mgx.gui.wizard.seqrun;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.TermAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.TermI;
import java.awt.Component;
import java.io.File;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

public final class SeqRunWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor> {

    // public static final String PROP_JOB = "job";
    // Example of invoking this wizard:
    // @ActionID(category="...", id="...")
    // @ActionRegistration(displayName="...")
    // @ActionReference(path="Menu/...")
    // public static ActionListener run() {
    //     return new ActionListener() {
    //         @Override public void actionPerformed(ActionEvent e) {
    //             WizardDescriptor wiz = new WizardDescriptor(new AnalysisWizardIterator());
    //             // {0} will be replaced by WizardDescriptor.Panel.getComponent().getName()
    //             // {1} will be replaced by WizardDescriptor.Iterator.name()
    //             wiz.setTitleFormat(new MessageFormat("{0} ({1})"));
    //             wiz.setTitle("...dialog title...");
    //             if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
    //                 ...do something...
    //             }
    //         }
    //     };
    // }
    //private int index;
    private WizardDescriptor wd = null;
    private SeqRunI seqrun = null;
    private int idx = 0;
    private String[] SEIndex;
    private String[] PEIndex;
    private String[] editIndex;
    private WizardDescriptor.Panel[] allPanels;
    private WizardDescriptor.Panel[] currentPanels;
    private WizardDescriptor.Panel[] SEPanels;
    private WizardDescriptor.Panel[] PEPanels;
    private WizardDescriptor.Panel[] editPanels;
    
    public static final String INVOCATION_MODE = "invocationMode";
    public static final String CREATE_MODE = "CREATE";
    public static final String EDIT_MODE = "EDIT";

    public SeqRunWizardIterator(){}
    
    public SeqRunWizardIterator(SeqRunI sr){        
        seqrun = sr;
    }
    
    public void setWizardDescriptor(WizardDescriptor wd) {
        this.wd = wd;
        wd.setTitleFormat(new MessageFormat("{0}"));
        wd.setTitle("Sequencing run wizard");
        createPanels();
        setData();
        if (seqrun != null){
            this.wd.putProperty(INVOCATION_MODE, EDIT_MODE);
            loadData();
        } else {
            this.wd.putProperty(INVOCATION_MODE, CREATE_MODE);
        }
    }

    @SuppressWarnings("unchecked")
    private void createPanels() {

        if (allPanels == null){
            allPanels = new WizardDescriptor.Panel[]{
                new SeqRunWizardPanel1(),
                new SeqRunWizardPanel2("Select sequence data", true),
                new SeqRunWizardPanel3(),
                new SeqRunWizardPanel2("Select forward sequence data", true),
                new SeqRunWizardPanel2("Select reverse sequence data", false)
            };
            
            String[] steps = new String[allPanels.length];
            
            for (int i = 0; i < allPanels.length; i++) {
                Component c = allPanels[i].getComponent();
                
                // Default step name to component name of panel
                steps[i] = c.getName();
                
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
                }
            }
            
            SEIndex = new String[] { steps[0], steps[1]};
            SEPanels = new WizardDescriptor.Panel[] {allPanels[0], allPanels[1]};
            
            PEIndex = new String[] { steps[0], steps[2], steps[3], steps[4]};
            PEPanels = new WizardDescriptor.Panel[]{allPanels[0], allPanels[2], allPanels[3], allPanels[4]};
            
            editIndex = new String[] { steps[0]};
            editPanels = new WizardDescriptor.Panel[] {allPanels[0]};
            
            if (seqrun != null)
                currentPanels = editPanels;
            else
                currentPanels = SEPanels;
        }
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
                ((SeqRunWizardPanel1)allPanels[0]).setMethods(methods.toArray(new TermI[]{}));
                ((SeqRunWizardPanel1)allPanels[0]).setPlatforms(platforms.toArray(new TermI[]{}));
                ((SeqRunWizardPanel1)allPanels[0]).setSeqRuns(m.SeqRun().fetchall());
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
    
    private void loadData(){
        wd.putProperty(SeqRunVisualPanel1.PROP_NAME, seqrun.getName());
        wd.putProperty(SeqRunVisualPanel1.PROP_METHOD, seqrun.getSequencingMethod());
        wd.putProperty(SeqRunVisualPanel1.PROP_PLATFORM, seqrun.getSequencingTechnology());
        wd.putProperty(SeqRunVisualPanel1.PROP_SUBMITTED, seqrun.getSubmittedToINSDC());
        wd.putProperty(SeqRunVisualPanel1.PROP_ACCESSION, seqrun.getAccession());
        wd.putProperty(SeqRunWizardIterator.INVOCATION_MODE, EDIT_MODE);
        ((SeqRunWizardPanel1) allPanels[0]).setProperties(wd);
    }

    private void setSingleEnd(boolean isSE){
        String[] contentData;
        
        if (isSE){
            currentPanels = SEPanels;
            contentData = SEIndex;
        } else {
            currentPanels = PEPanels;
            contentData = PEIndex;
        }
        
        wd.putProperty(WizardDescriptor.PROP_CONTENT_DATA, contentData);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public WizardDescriptor.Panel current() {
        return currentPanels[idx];        
    }

    @Override
    public String name() {
        if (idx == 0)
            return idx + 1 + " of ...";
        return idx + 1 + " of " + currentPanels.length;      
    }

    @Override
    public boolean hasNext() {
        return idx < currentPanels.length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return idx > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        
        if (idx == 0 && seqrun == null){
            if (isPairedEnd()){
                setSingleEnd(false);
            } else {
                setSingleEnd(true);
            }
        }
        
        idx++;
        wd.putProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, idx);
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        idx--;
        wd.putProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, idx);
    }
    
    public String getSeqRunName() {
        return (String) wd.getProperty(SeqRunVisualPanel1.PROP_NAME);
    }

    public TermI getSequencingMethod() {
        return (TermI) wd.getProperty(SeqRunVisualPanel1.PROP_METHOD);
    }

    public TermI getSequencingTechnology() {
        return (TermI) wd.getProperty(SeqRunVisualPanel1.PROP_PLATFORM);
    }

    public Boolean getSubmittedToINSDC() {
        return (Boolean) wd.getProperty(SeqRunVisualPanel1.PROP_SUBMITTED);
    }

    public String getAccession() {
        return (String) wd.getProperty(SeqRunVisualPanel1.PROP_ACCESSION);
    }

    public SeqRunI getSeqRun(MGXMasterI m) {
        // only to be used when editing an existing instance

        seqrun.setSequencingMethod((TermI) wd.getProperty(SeqRunVisualPanel1.PROP_METHOD))
                .setSequencingTechnology((TermI) wd.getProperty(SeqRunVisualPanel1.PROP_PLATFORM))
                .setSubmittedToINSDC((Boolean) wd.getProperty(SeqRunVisualPanel1.PROP_SUBMITTED))
                .setName((String) wd.getProperty(SeqRunVisualPanel1.PROP_NAME));

        if (seqrun.getSubmittedToINSDC()) {
            seqrun.setAccession((String) wd.getProperty(SeqRunVisualPanel1.PROP_ACCESSION));
        }
        return seqrun;
    }

    public File getForwardFile() {
        return (File)wd.getProperty(SeqRunVisualPanel2.PROP_FORWARD);
    }    
    
    public File getReverseFile() {
        return (File)wd.getProperty(SeqRunVisualPanel2.PROP_REVERSE);
    }
    
    public boolean isPairedEnd(){
        return getSequencingMethod().getName().equals("Paired-End");
    }
    
    public int getQualityThreshold(){
        return (Integer) wd.getProperty(SeqRunVisualPanel3.PROP_QTHRESHOLD);
    }
    
    public int getMinimalOverlap(){
        return (Integer) wd.getProperty(SeqRunVisualPanel3.PROP_MINOVERLAP);
    }
    
    public int getMaximalMismatches(){
        return (Integer) wd.getProperty(SeqRunVisualPanel3.PROP_MAXMISMATCHES);
    }


    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
}
