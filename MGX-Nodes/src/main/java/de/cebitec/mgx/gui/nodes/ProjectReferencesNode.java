package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.nodefactory.ReferenceNodeFactory;
import de.cebitec.mgx.gui.wizard.reference.InstallReferenceDescriptor;
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
public class ProjectReferencesNode extends MGXNodeBase<MGXMaster> {

    private ReferenceNodeFactory nf;

    public ProjectReferencesNode(final MGXMaster m) {
        this(new ReferenceNodeFactory(m), m);
        master = m;
        setDisplayName("Reference sequences");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectFiles.png");
    }

    private ProjectReferencesNode(ReferenceNodeFactory rnf, MGXMaster m) {
        super(Children.create(rnf, true), Lookups.fixed(m), m);
        nf = rnf;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action getPreferredAction() {
        return null;
    }

    @Override
    public Action[] getActions(boolean ctx) {
        return new Action[]{new AddGlobalReference()};
    }

    @Override
    public void updateModified() {
        //
    }
    
     private class AddGlobalReference extends AbstractAction {

        public AddGlobalReference() {
            putValue(NAME, "Add reference");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            InstallReferenceDescriptor wd = new InstallReferenceDescriptor();
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                final Reference ref = wd.getSelectedReference();
                SwingWorker<Long, Void> worker = new SwingWorker<Long, Void>() {
                    @Override
                    protected Long doInBackground() throws Exception {
                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                        return m.Reference().installGlobalReference(ref.getId());
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        nf.refreshChildren();
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
}
