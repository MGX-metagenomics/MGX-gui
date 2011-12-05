package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.nodefactory.HabitatNodeFactory;
import de.cebitec.mgx.gui.wizard.habitat.HabitatWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectDataNode extends MGXNodeBase {
    
    private HabitatNodeFactory hnf = null;
    
    public ProjectDataNode(MGXMaster m) {
        this(m, new HabitatNodeFactory(m));
        master = m;
    }
    
    private ProjectDataNode(MGXMaster m, HabitatNodeFactory hnf) {
        super(Children.create(hnf, true), Lookups.singleton(m));
        this.hnf = hnf;
        setDisplayName("Project Data");
    }
    
    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new AddHabitat()};
    }

    private class AddHabitat extends AbstractAction {

        public AddHabitat() {
            putValue(NAME, "Add habitat");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            HabitatWizardDescriptor hwd = new HabitatWizardDescriptor();
            Dialog dialog = DialogDisplayer.getDefault().createDialog(hwd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = hwd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                Habitat h = hwd.getHabitat();
                MGXMaster master = getLookup().lookup(MGXMaster.class);
                master.Habitat().create(h);
                hnf.refreshChildren();
            }
        }
    }
}
