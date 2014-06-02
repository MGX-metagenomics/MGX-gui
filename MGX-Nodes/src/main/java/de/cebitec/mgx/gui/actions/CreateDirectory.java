package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.List;
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

    private final FileNodeFactory fnf;

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
                String targetPath = currentDir.getFullPath() + MGXFile.separator + dirName;
                final MGXFile newDir = new MGXFile(m, targetPath, true, 0);
                newDir.setParent(currentDir);

                SwingWorker<Boolean, String> sw = new SwingWorker<Boolean, String>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        try {
                            return m.File().createDirectory(newDir);
                        } catch (MGXServerException ex) {
                            if (ex.getMessage().endsWith("already exists.")) {
                                publish(ex.getMessage());
                            }
                        }
                        return false;
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        for (final String msg : chunks) {
                            EventQueue.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    NotifyDescriptor nd = new NotifyDescriptor(msg, "Error",
                                            NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
                                    DialogDisplayer.getDefault().notify(nd);
                                }
                            });

                        }
                        super.process(chunks);
                    }

                    @Override
                    protected void done() {
                        try {
                            if (get()) {
                                fnf.refreshChildren();
                            }
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
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
