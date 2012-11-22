package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.actions.CreateDirectory;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class ProjectFilesNode extends MGXNodeBase<MGXMaster> {
    
    private FileNodeFactory nf;

    public ProjectFilesNode(MGXMaster m, MGXFile root) {
        this(new FileNodeFactory(m, root), m, root);
        master = m;
        setDisplayName("Project Files");
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/ProjectFiles.png");
    }
    
    private ProjectFilesNode(FileNodeFactory fnf, MGXMaster m, MGXFile root) {
        super(Children.create(fnf, true), Lookups.fixed(m, root), m);
        nf = fnf;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[]{new CreateDirectory(nf)}; //, new DeleteDirEntry()};
    }

    @Override
    public void updateModified() {
        //
    }

//    public class CreateDirectory extends AbstractAction {
//
//        public CreateDirectory() {
//            putValue(NAME, "Create directory");
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
//            MGXFile currentDir = Utilities.actionsGlobalContext().lookup(MGXFile.class);
//            assert currentDir.isDirectory();
//            NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine("Directory name:", "Choose directory name");
//            if (NotifyDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(nd))) {
//                String dirName = nd.getInputText().trim();
//                if (!dirName.isEmpty()) {
//                    final MGXFile newDir = new MGXFile();
//                    newDir.setName(dirName);
//                    newDir.isDirectory(true);
//                    newDir.setParent(currentDir);
//
//                    SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
//                        @Override
//                        protected Void doInBackground() throws Exception {
//                            m.File().createDirectory(newDir);
//                            return null;
//                        }
//
//                        @Override
//                        protected void done() {
//                            try {
//                                get();
//                            } catch (InterruptedException | ExecutionException ex) {
//                                Exceptions.printStackTrace(ex);
//                            }
//                            nf.refreshChildren();
//                            super.done();
//                        }
//                    };
//                    sw.execute();
//                }
//            }
//        }
//
//        @Override
//        public boolean isEnabled() {
//            MGXFile currentDir = Utilities.actionsGlobalContext().lookup(MGXFile.class);
//            return (super.isEnabled() && currentDir.isDirectory() && RBAC.isUser());
//        }
//    }
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
