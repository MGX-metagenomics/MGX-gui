package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import de.cebitec.mgx.gui.wizard.sample.SampleWizardDescriptor;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.IOException;
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
public class MGXDirectoryNode extends MGXNodeBase<MGXFile> {

    private FileNodeFactory nf = null;

    public MGXDirectoryNode(MGXMaster m, MGXFile f) {
        this(f, m, new FileNodeFactory(m, f));
    }

    private MGXDirectoryNode(MGXFile f, MGXMaster m, FileNodeFactory fnf) {
        super(Children.create(fnf, true), Lookups.fixed(m, f), f);
        master = m;
        setDisplayName(f.getName());
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
        setShortDescription(f.getName());
        this.nf = fnf;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new UploadFile()};
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getContent().getName());
    }

    private class UploadFile extends AbstractAction {

        public UploadFile() {
            putValue(NAME, "Upload file");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SampleWizardDescriptor wd = new SampleWizardDescriptor();
            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
            dialog.setVisible(true);
            dialog.toFront();
            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
            if (!cancelled) {
                Habitat hab = getLookup().lookup(Habitat.class);
                final Sample s = wd.getSample();
                s.setHabitatId(hab.getId());
                SwingWorker<Long, Void> worker = new SwingWorker<Long, Void>() {
                    @Override
                    protected Long doInBackground() throws Exception {
                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
                        return m.Sample().create(s);
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
