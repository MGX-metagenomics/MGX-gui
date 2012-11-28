package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.nodes.MGXDirectoryNode;
import de.cebitec.mgx.gui.nodes.MGXFileNode;
import java.beans.PropertyChangeEvent;
import java.util.List;
import org.openide.nodes.*;

/**
 *
 * @author sj
 */
public class FileNodeFactory extends ChildFactory<MGXFile> implements NodeListener {

    private MGXMaster master;
    private MGXFile curDirectory;
    //
    private boolean refreshing = false;

    public FileNodeFactory(MGXMaster master, MGXFile curDir) {
        this.master = master;
        curDirectory = curDir;
    }

    @Override
    protected boolean createKeys(List<MGXFile> toPopulate) {
        for (MGXFile f : master.File().fetchall(curDirectory)) {
            toPopulate.add(f);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(MGXFile file) {
        Node node;
        if (!file.isDirectory()) {
            node = new MGXFileNode(file, master);
        } else {
            node = new MGXDirectoryNode(master, file);
        }
        node.addNodeListener(this);
        return node;
    }

    public void refreshChildren() {
        if (!refreshing) {
            refreshing = true;
            refresh(true);
            refreshing = false;
        }
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        //refresh(true);
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        //refresh(true);
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        // this is ugly, and unnecessary everywhere else. however, here
        // it triggers a stack overflow otherwise: refresh() makes the
        // childfactory remove (and re-add) all nodes, which triggers a 
        // nodeDestroyed() call for each removed node.
        //
        // I have no idea....
        if (!refreshing) {
            refreshing = true;
            refresh(true);
            refreshing = false;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //refresh(true);
    }
}
