package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.nodes.MGXFileNode;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author sjaenick
 */
public class FileNodeFactory extends ChildFactory<MGXFile> implements NodeListener {

    private final MGXMaster master;
    private final MGXFile current_root;

    public FileNodeFactory(MGXMaster m, MGXFile d) {
        master = m;
        current_root = d;
    }

    @Override
    protected boolean createKeys(List<MGXFile> toPopulate) {
        toPopulate.addAll(master.File().fetchall(current_root));
        return true;
    }

    @Override
    protected Node createNodeForKey(MGXFile file) {
        MGXFileNode node;
        if (!file.isDirectory()) {
            node = new MGXFileNode(master, file);
            node.addNodeListener(this);
        } else {
            FileNodeFactory dirEntryNodeFactory = new FileNodeFactory(master, file);
            node = new MGXFileNode(master, file, dirEntryNodeFactory);
            node.addNodeListener(dirEntryNodeFactory);
        }
        return node;
    }

    public void refreshChildren() {
        refresh(true);
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        this.refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
    }
}
