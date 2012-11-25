//package de.cebitec.mgx.gui.old;
//
//import de.cebitec.mgx.gui.actions.CreateDirectory;
//import de.cebitec.mgx.gui.actions.DeleteFileOrDirectory;
//import de.cebitec.mgx.gui.controller.MGXMaster;
//import de.cebitec.mgx.gui.datamodel.MGXFile;
//import de.cebitec.mgx.gui.old.FileNodeFactory;
//import javax.swing.Action;
//import org.openide.nodes.Children;
//import org.openide.util.lookup.Lookups;
//
///**
// *
// * @author sj
// */
//public class MGXDirectoryNode extends MGXNodeBase<MGXFile> {
//
//    private FileNodeFactory nf = null;
//
//    public MGXDirectoryNode(MGXMaster m, MGXFile d, FileNodeFactory nf) {
//        super(Children.create(nf, true), Lookups.fixed(m, d), d);
//        assert d.isDirectory();
//        this.nf = nf;
//        setDisplayName(stripPath(d.getName()));
//        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
//    }
//
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
//        return new Action[]{new CreateDirectory(nf), new DeleteFileOrDirectory()};
//    }
//
//    @Override
//    public void updateModified() {
//        setDisplayName(stripPath(getContent().getName()));
//        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
//    }
//
//    private static String stripPath(String in) {
//        String[] split = in.split("/");
//        return split[split.length - 1];
//    }
//
////    private class UploadFile extends AbstractAction {
////
////        public UploadFile() {
////            putValue(NAME, "Upload file");
////        }
////
////        @Override
////        public void actionPerformed(ActionEvent e) {
////            final SeqRunWizardDescriptor wd = new SeqRunWizardDescriptor();
////            Dialog dialog = DialogDisplayer.getDefault().createDialog(wd);
////            dialog.setVisible(true);
////            dialog.toFront();
////            boolean cancelled = wd.getValue() != WizardDescriptor.FINISH_OPTION;
////            if (!cancelled) {
////
////                final String selectedFile = null;
////
////                SwingWorker<Void, Exception> sw = new SwingWorker<Void, Exception>() {
////                    @Override
////                    protected Void doInBackground() {
////                        MGXMaster m = Utilities.actionsGlobalContext().lookup(MGXMaster.class);
////                        MGXFile directory = getLookup().lookup(MGXFile.class);
////                        assert directory.isDirectory();
////                        try {
////                            FileReader fr = new FileReader(selectedFile);
////                            final FileUploader uploader = m.File().createUploader(fr, directory);
////                            
////                            MGXTask run = new MGXTask() {
////                                @Override
////                                public void process() {
////                                    boolean success = uploader.upload();
////                                    if (!success) {
////                                        publish(new MGXClientException(uploader.getErrorMessage()));
////                                    }
////                                }
////
////                                @Override
////                                public void finished() {
////                                    nf.refreshChildren();
////                                }
////
////                                @Override
////                                public void failed() {
////                                    nf.refreshChildren();
////                                }
////
////                                @Override
////                                public void propertyChange(PropertyChangeEvent pce) {
////                                    if (pce.getPropertyName().equals(UploadBase.NUM_ELEMENTS_SENT)) {
////                                        setStatus(String.format("%1$d bytes sent", pce.getNewValue()));
////                                    }
////                                }
////                            };
////                            uploader.addPropertyChangeListener(run);
////
////                            TaskManager.getInstance().addTask("Upload " + selectedFile, run);
////                        } catch (FileNotFoundException ex) {
////                            publish(new MGXClientException(ex.getMessage()));
////                        }
////
////                        return null;
////                    }
////
////                    @Override
////                    protected void process(List<Exception> chunks) {
////                        StringBuilder sb = new StringBuilder();
////                        for (Exception e : chunks) {
////                            sb.append(e.getMessage());
////                        }
////                        NotifyDescriptor nd = new NotifyDescriptor(sb.toString(),
////                                "Error",
////                                NotifyDescriptor.OK_CANCEL_OPTION,
////                                NotifyDescriptor.ERROR_MESSAGE,
////                                null,
////                                null);
////                        DialogDisplayer.getDefault().notify(nd);
////                    }
////
////                    @Override
////                    protected void done() {
////                        try {
////                            get();
////                        } catch (InterruptedException | ExecutionException ex) {
////                            Exceptions.printStackTrace(ex);
////                        }
////                        super.done();
////                    }
////                };
////                sw.execute();
////            }
////        }
////
////        @Override
////        public boolean isEnabled() {
////            return (super.isEnabled() && RBAC.isUser());
////        }
////    }
//}
