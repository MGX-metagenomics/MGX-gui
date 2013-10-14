package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.client.datatransfer.UploadBase;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.actions.UploadReference;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.nodefactory.ReferenceNodeFactory;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import static de.cebitec.mgx.gui.taskview.MGXTask.TASK_FAILED;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.reference.InstallReferenceDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
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
                final Reference ref = wd.getSelectedReference();
                final MGXMaster master = Utilities.actionsGlobalContext().lookup(MGXMaster.class);


                final MGXTask run = new MGXTask("Install " + ref.getName()) {
                    private String err = null;

                    @Override
                    public boolean process() {
                        try {
                            setStatus("Installing reference");
                            master.Reference().installGlobalReference(ref.getId());
                        } catch (MGXServerException ex) {
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
