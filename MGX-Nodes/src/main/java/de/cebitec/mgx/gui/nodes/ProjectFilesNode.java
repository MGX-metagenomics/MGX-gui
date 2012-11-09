package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.controller.RBAC;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Children;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectFilesNode extends MGXNodeBase<MGXMaster> {

    public ProjectFilesNode(MGXMaster m, MGXFile root) {
        super(Children.create(new FileNodeFactory(m, root), true), Lookups.fixed(m, root), m);
        master = m;
        setDisplayName("Project Files");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectFiles.png");
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{new CreateDirectory()}; //, new DeleteDirEntry()};
    }

    @Override
    public void updateModified() {
        //
    }

    private class CreateDirectory extends AbstractAction {

        public CreateDirectory() {
            putValue(NAME, "Create directory");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
            MGXFile currentDir = Utilities.actionsGlobalContext().lookup(MGXFile.class);
            assert currentDir.isDirectory();
            NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Directory name:", "Choose directory name");
            if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(nd))) {
                String dirName = nd.getInputText().trim();
                if (!dirName.isEmpty()) {
                    MGXFile newDir = new MGXFile();
                    newDir.setName(dirName);
                    newDir.isDirectory(true);
                    newDir.setParent(currentDir);
                    m.File().createDirectory(newDir);
                }
            }
        }

        @Override
        public boolean isEnabled() {
            MGXFile currentDir = Utilities.actionsGlobalContext().lookup(MGXFile.class);
            return (super.isEnabled() && currentDir.isDirectory() && RBAC.isUser());
        }
    }
//    private class DeleteDirEntry extends AbstractAction {
//
//        public DeleteDirEntry() {
//            putValue(NAME, "Delete");
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
//        }
//
//        @Override
//        public boolean isEnabled() {
//            return (super.isEnabled() && RBAC.isUser());
//        }
//    }
}
