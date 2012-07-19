package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.DirEntry;
import de.cebitec.mgx.gui.nodes.DirEntryNode;
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
public class DirEntryNodeFactory extends ChildFactory<DirEntry> implements NodeListener {

    private MGXMaster master;
    private DirEntry current_root = null;

    public DirEntryNodeFactory(MGXMaster m, DirEntry d) {
        master = m;
        current_root = d;
    }

    public DirEntryNodeFactory(MGXMaster m) {
        master = m;
    }

    @Override
    protected boolean createKeys(List<DirEntry> toPopulate) {
        if (current_root == null) {
            toPopulate.addAll(master.File().fetchall());
        } else {
            toPopulate.addAll(current_root.getDirectory().getEntries());
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DirEntry key) {
        DirEntryNode node;
        if (key.isFile()) {
            node = new DirEntryNode(master, key);
            node.addNodeListener(this);
        } else {
            DirEntryNodeFactory dirEntryNodeFactory = new DirEntryNodeFactory(master, key);
            node = new DirEntryNode(master, key, dirEntryNodeFactory);
            node.addNodeListener(dirEntryNodeFactory);
        }
        return node;
    }

    public void refreshChildren() {
        refresh(true);
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        refresh(true);
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        refresh(true);
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //refresh(true);
    }
}
