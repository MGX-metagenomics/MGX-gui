package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.gui.nodes.MGXDirectoryNode;
import de.cebitec.mgx.gui.nodes.MGXFileNode;
import de.cebitec.mgx.gui.nodes.MGXNodeBase;
import java.util.Iterator;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class FileNodeFactory extends MGXNodeFactoryBase<MGXFileI, MGXFileI> {

    public FileNodeFactory(MGXFileI curDir) {
        super(curDir);
    }

    @Override
    protected boolean addKeys(List<MGXFileI> toPopulate) {
        try {
            Iterator<MGXFileI> iter = getContent().getMaster().File().fetchall(getContent());
            while (iter.hasNext()) {
                toPopulate.add(iter.next());
            }
            return true;
        } catch (MGXException ex) {
            // a refresh might occur while the directory is being deleted
            // on another thread
            if (getContent().isDeleted()) {
                toPopulate.clear();
                return true;
            }
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    @Override
    protected MGXNodeBase<MGXFileI> createNodeFor(MGXFileI file) {
        if (!file.isDirectory()) {
            return new MGXFileNode(file);
        } else {
            return new MGXDirectoryNode(file);
        }
    }
}
