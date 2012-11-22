package de.cebitec.mgx.gui.actions;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import de.cebitec.mgx.gui.nodes.MGXFileNode;
import java.awt.event.ActionEvent;
import java.io.IOException;
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
public class DeleteFileOrDirectory extends AbstractAction {

    private MGXFileNode node;

    public DeleteFileOrDirectory(MGXFileNode nf) {
        putValue(NAME, "Delete");
        node = nf;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
        final MGXFile file = Utilities.actionsGlobalContext().lookup(MGXFile.class);

        NotifyDescriptor d = new NotifyDescriptor("Really delete " + file.getName() + "?",
                "Delete file/directory",
                NotifyDescriptor.YES_NO_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE,
                null,
                null);
        Object ret = DialogDisplayer.getDefault().notify(d);
        if (NotifyDescriptor.YES_OPTION.equals(ret)) {
            SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    m.File().delete(file);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    super.done();
                }
            };
            sw.execute();
        }
    }

    @Override
    public boolean isEnabled() {
        return (super.isEnabled() && RBAC.isUser());
    }
}
