package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.nodes.MGXDirectoryNode;
import de.cebitec.mgx.gui.nodes.MGXFileNode;
import de.cebitec.mgx.gui.swingutils.NonEDT;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.*;

/**
 *
 * @author sj
 */
public class FileNodeFactory extends MGXNodeFactoryBase<MGXFile> {

    private final MGXMaster master;
    private final MGXFile curDirectory;
    //

    public FileNodeFactory(MGXMaster master, MGXFile curDir) {
        this.master = master;
        curDirectory = curDir;
    }

    @Override
    protected boolean createKeys(List<MGXFile> toPopulate) {
        Iterator<MGXFile> iter = master.File().fetchall(curDirectory);
        while (iter.hasNext()) {
            toPopulate.add(iter.next());
        }
        Collections.sort(toPopulate);
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
