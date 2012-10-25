package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.nodefactory.HabitatNodeFactory;
import de.cebitec.mgx.gui.wizard.habitat.HabitatWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectDataNode extends MGXNodeBase<MGXMaster> {

    private HabitatNodeFactory hnf = null;

    public ProjectDataNode(MGXMaster m) {
        this(m, new HabitatNodeFactory(m));
    }

    private ProjectDataNode(MGXMaster m, HabitatNodeFactory hnf) {
        super(Children.create(hnf, true), Lookups.fixed(m), m);
        master = m;
        this.hnf = hnf;
        setDisplayName("Project Data");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectData.png");
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new AddHabitat(), new Refresh()};
    }

    @Override
    public void updateModified() {
        //
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
                final Habitat h = hwd.getHabitat();
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                        m.Habitat().create(h);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        hnf.refreshChildren();
                        super.done();
                    }
                };
                worker.execute();
            }
        }
    }

    private class Refresh extends AbstractAction {

        public Refresh() {
            putValue(NAME, "Refresh");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            hnf.refreshChildren();
        }
    }
}
