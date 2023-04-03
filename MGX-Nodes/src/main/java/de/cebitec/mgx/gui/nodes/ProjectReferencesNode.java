package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.nodeactions.UploadReference;
import de.cebitec.mgx.gui.rbac.RBAC;
import de.cebitec.mgx.gui.nodefactory.ReferenceNodeFactory;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import de.cebitec.mgx.gui.taskview.MGXTask;
import de.cebitec.mgx.gui.taskview.TaskManager;
import de.cebitec.mgx.gui.wizard.reference.InstallReferenceDescriptor;
import java.awt.Dialog;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.Serial;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectReferencesNode extends AbstractNode {

    public ProjectReferencesNode(final MGXMasterI m) {
        this(new ReferenceNodeFactory(m), m);
        super.setDisplayName("Reference sequences");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/files.svg");
    }

    private ProjectReferencesNode(ReferenceNodeFactory rnf, MGXMasterI m) {
        super(Children.create(rnf, true), Lookups.fixed(m));
    }

    @Override
    public Image getIcon(int type) {
        Image image = super.getIcon(type);
        Image scaledInstance = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return scaledInstance;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean ctx) {
        return new Action[]{new AddGlobalReference(), new UploadReference()};
    }

//    @Override
//    public void updateModified() {
//        //
//    }
    private static class AddGlobalReference extends AbstractAction {

        @Serial
        private static final long serialVersionUID = 1L;

        public AddGlobalReference() {
            super.putValue(NAME, "Add reference");
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
                        TaskI<MGXReferenceI> serverTask = null;
                        try {
                            setStatus("Installing reference");
                            serverTask = master.Reference().installGlobalReference(ref);
                            while (serverTask != null && !serverTask.done()) {
                                sleep();
                                master.<MGXReferenceI>Task().refresh(serverTask);
                            }
                            // we do not call serverTask.finished() since this would
                            // trigger a propertyChangeEvent for ref, i.e. the global
                            // reference
                        } catch (MGXException ex) {
                            err = ex.getMessage();
                            return false;
                        }
                        if (serverTask != null && !serverTask.getState().equals(TaskI.State.FINISHED)) {
                            err = serverTask.getStatusMessage();
                        }
                        return serverTask != null && serverTask.getState().equals(TaskI.State.FINISHED);
                    }

                    @Override
                    public void finished() {
                        super.finished();
                        master.childChanged();
                    }

                    @Override
                    public void failed(String reason) {
                        setStatus(err);
                        super.failed(reason);
                        master.childChanged();
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
