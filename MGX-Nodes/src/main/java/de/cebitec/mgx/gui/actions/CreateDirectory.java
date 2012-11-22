package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.SwingWorker;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class CreateDirectory extends AbstractAction {

    private FileNodeFactory fnf;

    public CreateDirectory(FileNodeFactory nf) {
        putValue(NAME, "Create directory");
        fnf = nf;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        MGXFile currentDir = Utilities.actionsGlobalContext().lookup(MGXFile.class);
        assert currentDir.isDirectory();
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Directory name:", "Choose directory name");
        if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(nd))) {
            String dirName = nd.getInputText().trim();
            if (!dirName.isEmpty()) {
                final MGXFile newDir = new MGXFile();
                newDir.setName(dirName);
                newDir.isDirectory(true);
                newDir.setParent(currentDir);

                SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        m.File().createDirectory(newDir);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        fnf.refreshChildren();
                        super.done();
                    }
                };
                sw.execute();
            }
        }
    }

    @Override
    public boolean isEnabled() {
        MGXFile currentDir = Utilities.actionsGlobalContext().lookup(MGXFile.class);
        return (super.isEnabled() && currentDir.isDirectory() && RBAC.isUser());
    }
}
