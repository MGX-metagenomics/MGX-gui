//package de.cebitec.mgx.gui.old;
//
//import de.cebitec.mgx.gui.controller.MGXMaster;
//import de.cebitec.mgx.gui.datamodel.MGXFile;
//import de.cebitec.mgx.gui.nodes.MGXDirectoryNode;
//import de.cebitec.mgx.gui.nodes.MGXFileNode;
//import java.beans.PropertyChangeEvent;
//import java.util.List;
//import org.openide.nodes.ChildFactory;
//import org.openide.nodes.Node;
//import org.openide.nodes.NodeEvent;
//import org.openide.nodes.NodeListener;
//import org.openide.nodes.NodeMemberEvent;
//import org.openide.nodes.NodeReorderEvent;
//
///**
// *
// * @author sjaenick
// */
//public class FileNodeFactory extends ChildFactory<MGXFile> implements NodeListener {
//
//    private final MGXMaster master;
//    private final MGXFile current_root;
//
//    public FileNodeFactory(MGXMaster m, MGXFile d) {
//        master = m;
//        current_root = d;
//        assert current_root.isDirectory();
//    }
//
//    @Override
//    protected boolean createKeys(List<MGXFile> toPopulate) {
//        toPopulate.addAll(master.File().fetchall(current_root));
//        return true;
//    }
//
//    @Override
//    protected Node createNodeForKey(MGXFile file) {
//        Node node;
//        if (!file.isDirectory()) {
//            node = new MGXFileNode(master, file);
//        } else {
//            FileNodeFactory dirEntryNodeFactory = new FileNodeFactory(master, file);
//            node = new MGXDirectoryNode(master, file, dirEntryNodeFactory);
//        }
//        node.addNodeListener(this);
//        return node;
//    }
//
//    public void refreshChildren() {
//        refresh(true);
//    }
//
//    @Override
//    public void childrenAdded(NodeMemberEvent ev) {
//    }
//
//    @Override
//    public void childrenRemoved(NodeMemberEvent ev) {
//    }
//
//    @Override
//    public void childrenReordered(NodeReorderEvent ev) {
//    }
//
//    @Override
//    public void nodeDestroyed(NodeEvent ev) {
//        this.refresh(true);
//    }
//
//    @Override
//    public void propertyChange(PropertyChangeEvent pce) {
//    }
//}
