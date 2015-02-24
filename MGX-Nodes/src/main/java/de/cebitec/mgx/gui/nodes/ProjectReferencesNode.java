package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.actions.UploadReference;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.nodefactory.ReferenceNodeFactory;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.reference.InstallReferenceDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.Children;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectReferencesNode extends MGXNodeBase<MGXMasterI> {

    private ReferenceNodeFactory nf;

    public ProjectReferencesNode(final MGXMasterI m) {
        this(new ReferenceNodeFactory(m), m);
        setDisplayName("Reference sequences");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectFiles.png");
    }

    private ProjectReferencesNode(ReferenceNodeFactory rnf, MGXMasterI m) {
        super(m, Children.create(rnf, true), Lookups.fixed(m), m);
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
        return new Action[]{new AddGlobalReference(), new UploadReference(nf)};
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
                final MGXReferenceI ref = wd.getSelectedReference();
                final MGXMasterI master = Utilities.actionsGlobalContext().lookup(MGXMasterI.class);


                final MGXTask run = new MGXTask("Install " + ref.getName()) {
                    private String err = null;

                    @Override
                    public boolean process() {
                        try {
                            setStatus("Installing reference");
                            long refId = master.Reference().installGlobalReference(ref.getId());
                        } catch (MGXException ex) {
                            err = ex.getMessage();
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void finished() {
                        super.finished();
                        nf.refreshChildren();
                    }

                    @Override
                    public void failed() {
                        super.failed();
                        setStatus("Failed: " + err);
                        nf.refreshChildren();
                    }
                };

                NonEDT.invoke(new Runnable() {
                    @Override
                    public void run() {
                        TaskManager.getInstance().addTask(run);

                    }
                });
            }
        }

        @Override
        public boolean isEnabled() {
            return (super.isEnabled() && RBAC.isUser());
        }
    }
}
