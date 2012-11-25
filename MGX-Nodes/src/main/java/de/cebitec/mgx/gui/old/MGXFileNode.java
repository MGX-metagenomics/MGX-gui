//package de.cebitec.mgx.gui.old;
//
//import de.cebitec.mgx.gui.actions.CreateDirectory;
//import de.cebitec.mgx.gui.actions.DeleteFileOrDirectory;
//import de.cebitec.mgx.gui.controller.MGXMaster;
//import de.cebitec.mgx.gui.controller.RBAC;
//import de.cebitec.mgx.gui.datamodel.MGXFile;
//import de.cebitec.mgx.gui.taskview.MGXTask;
//import de.cebitec.mgx.gui.taskview.TaskManager;
//import java.awt.event.ActionEvent;
//import javax.swing.AbstractAction;
//import javax.swing.Action;
//import org.openide.DialogDisplayer;
//import org.openide.NotifyDescriptor;
//import org.openide.nodes.Children;
//import org.openide.util.Utilities;
//import org.openide.util.lookup.Lookups;
//
///**
// *
// * @author sjaenick
// */
//public class MGXFileNode extends MGXNodeBase<MGXFile> {
//
////    private FileNodeFactory nf = null;
////
////    public MGXFileNode(MGXMaster m, MGXFile d, FileNodeFactory nf) {
////        super(Children.create(nf, true), Lookups.fixed(m, d), d);
////        assert d.isDirectory();
////        this.nf = nf;
////        setDisplayName(stripPath(d.getName()));
////        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
////    }
//    public MGXFileNode(MGXMaster m, MGXFile f) {
//        super(Children.LEAF, Lookups.fixed(m, f), f);
//        assert !f.isDirectory();
//        setDisplayName(stripPath(f.getName()));
//        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/File.png");
//    }
//
////    @Override
////    public void destroy() throws IOException {
////        super.destroy();
////        fireNodeDestroyed();
////    }
//    @Override
//    public boolean canDestroy() {
//        return true;
//    }
//
//    @Override
//    public Action getPreferredAction() {
//        return null;
//    }
//
//    @Override
//    public Action[] getActions(boolean context) {
//        return new Action[]{new DeleteFile()};
//    }
//
//    private static String stripPath(String in) {
//        String[] split = in.split("/");
//        return split[split.length - 1];
//    }
//
//    @Override
//    public void updateModified() {
//        setDisplayName(stripPath(getContent().getName()));
//        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/File.png");
//    }
//
//    private class DeleteFile extends AbstractAction {
//
//        public DeleteFile() {
//            putValue(NAME, "DeleteFile");
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            final MGXFile f = getLookup().lookup(MGXFile.class);
//            NotifyDescriptor d = new NotifyDescriptor("Really delete file " + f.getName() + "?",
//                    "Delete file",
//                    NotifyDescriptor.YES_NO_OPTION,
//                    NotifyDescriptor.QUESTION_MESSAGE,
//                    null,
//                    null);
//            Object ret = DialogDisplayer.getDefault().notify(d);
//            final MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
//            if (NotifyDescriptor.YES_OPTION.equals(ret)) {
//                MGXTask deleteTask = new MGXTask() {
//                    @Override
//                    public void process() {
//                        setStatus("Deleting..");
//                        m.File().delete(f);
//                    }
//
//                    @Override
//                    public void finished() {
//                        super.finished();
//                        //fireNodeDestroyed();
//                    }
//                };
//
//                TaskManager.getInstance().addTask("Delete " + f.getName(), deleteTask);
//            }
//        }
//
//        @Override
//        public boolean isEnabled() {
//            return (super.isEnabled() && RBAC.isUser());
//        }
//    }
//}
