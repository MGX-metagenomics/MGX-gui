package de.cebitec.mgx.gui.goldstandard.wizards.selectjobs;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;

public final class SelectJobsWizardAction implements ActionListener {

    private WizardDescriptor wizardDescriptor;
    private WizardDescriptor.Panel<WizardDescriptor>[] panels;
    private SeqRunI currentRun;
    private boolean hierarchicAT;
    
    public void setRun(SeqRunI newRun) {
        currentRun = newRun;
    }
    
    public void setHierarchicAT(boolean hierarchic){
        hierarchicAT = hierarchic;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            wizardDescriptor = new WizardDescriptor(getPanels());
            // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
            wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
            wizardDescriptor.setTitle("Your wizard dialog title here");
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                // do something
            }
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Initialize panels representing individual wizard's steps and sets various
     * properties for them influencing wizard appearance.
     */
    @SuppressWarnings("unchecked")
    private WizardDescriptor.Panel<WizardDescriptor>[] getPanels() throws MGXException {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                new SelectJobsWizardPanel1(currentRun, hierarchicAT, Integer.MAX_VALUE, false),
            };
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", i);
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }
    
    public JobI getJob(){
        return (JobI)wizardDescriptor.getProperty(SelectJobsVisualPanel1.PROP_JOBS);
    }
    
    public AttributeTypeI getAttributeType(){
        return (AttributeTypeI)wizardDescriptor.getProperty(SelectJobsVisualPanel1.PROP_ATTRIBUTETYPE);
    }
}
