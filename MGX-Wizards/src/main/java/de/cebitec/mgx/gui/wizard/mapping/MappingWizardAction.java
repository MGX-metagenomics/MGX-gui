/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.wizard.mapping;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.wizard.mapping.MappingVisualPanel1.MappingEntry;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import de.cebitec.mgx.gui.mapping.viewer.TopComponentViewer;

// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
@ActionID(category = "...", id = "wizard.wizardWizardAction")
@ActionRegistration(displayName = "Mapping")
@ActionReference(path = "Menu/Window", position = 1)
public final class MappingWizardAction implements ActionListener {

    private final Lookup.Result<MGXMaster> mgxMasterResult;
    private MGXMaster currentMaster;

    private MappingWizardAction() {
        mgxMasterResult = Utilities.actionsGlobalContext().lookupResult(MGXMaster.class);
        currentMaster = loadMGXMaster();
    }

    private MGXMaster loadMGXMaster() {
        for (MGXMaster newMaster : mgxMasterResult.allInstances()) {
            if (newMaster != null) {
                return newMaster;
            }
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<WizardDescriptor.FinishablePanel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.FinishablePanel<WizardDescriptor>>();
        panels.add(new MappingWizardPanel1(currentMaster));
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.FINISH_OPTION, true);
                jc.putClientProperty(WizardDescriptor.PROP_OPTION_TYPE, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator(panels));
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Mapping Wizard");
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            MappingEntry mapping = (MappingEntry) wiz.getProperty(MappingVisualPanel1.PROP_MAPPING); 
            if (TopComponentViewer.getInstance() != null) {
                TopComponentViewer.getInstance().close();
            }
            TopComponentViewer component = new TopComponentViewer(mapping.getReference(), mapping.getMapping());
            
           
            component.open();
        }
    }
}
