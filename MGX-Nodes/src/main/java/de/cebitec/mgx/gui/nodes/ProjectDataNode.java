package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.gui.controller.RBAC;
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
public class ProjectDataNode extends MGXNodeBase<MGXMasterI, ProjectDataNode> {

    private HabitatNodeFactory hnf = null;

    public ProjectDataNode(MGXMasterI m) {
        this(m, new HabitatNodeFactory(m));
    }

    private ProjectDataNode(MGXMasterI m, HabitatNodeFactory hnf) {
        super(m, Children.create(hnf, true), Lookups.fixed(m), m);
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
            final HabitatWizardDescriptor hwd = new HabitatWizardDescriptor();
            Dialog dialog = DialogDisplayer.getDefault().createDialog(hwd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = hwd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                SwingWorker<HabitatI, Void> worker = new SwingWorker<HabitatI, Void>() {
                    @Override
                    protected HabitatI doInBackground() throws Exception {
                        MGXMasterI m = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);
                        return m.Habitat().create(hwd.getHabitatName(), hwd.getHabitatLatitude(), hwd.getHabitatLongitude(), hwd.getHabitatAltitude(), hwd.getHabitatBiome(), hwd.getHabitatDescription());
                    }

                    @Override
                    protected void done() {
                        HabitatI h = null;
                        try {
                            h = get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        if (h != null) {
                            hnf.refreshChildren();
                        }
                        super.done();
                    }
                };
                worker.execute();
            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
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
