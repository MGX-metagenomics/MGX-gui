package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.nodes.MGXDirectoryNode;
import de.cebitec.mgx.gui.nodes.MGXFileNode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class FileNodeFactory extends MGXNodeFactoryBase<MGXFileI> {

    private final MGXFileI curDirectory;
    //

    public FileNodeFactory(MGXFileI curDir) {
        super(curDir.getMaster());
        curDirectory = curDir;
    }

    @Override
    protected boolean addKeys(List<MGXFileI> toPopulate) {
        try {
            Iterator<MGXFileI> iter = getMaster().File().fetchall(curDirectory);
            while (iter.hasNext()) {
                toPopulate.add(iter.next());
            }
            Collections.sort(toPopulate);
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    @Override
    protected Node createNodeForKey(MGXFileI file) {
        Node node;
        if (!file.isDirectory()) {
            node = new MGXFileNode(file);
        } else {
            node = new MGXDirectoryNode(file);
        }
        node.addNodeListener(this);
        return node;
    }

//    @Override
//    public void childrenAdded(NodeMemberEvent ev) {
//        //refresh(true);
//    }
//
//    @Override
//    public void childrenRemoved(NodeMemberEvent ev) {
//        //refresh(true);
//    }
//
//    @Override
//    public void childrenReordered(NodeReorderEvent ev) {
//    }
//
//    @Override
//    public void nodeDestroyed(NodeEvent ev) {
//        // this is ugly, and unnecessary everywhere else. however, here
//        // it triggers a stack overflow otherwise: refresh() makes the
//        // childfactory remove (and re-add) all nodes, which triggers a 
//        // nodeDestroyed() call for each removed node.
//        //
//        // I have no idea....
//        if (!refreshing) {
//            refreshing = true;
//            refresh(true);
//            refreshing = false;
//        }
//    }
//
//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        //refresh(true);
//    }
}
