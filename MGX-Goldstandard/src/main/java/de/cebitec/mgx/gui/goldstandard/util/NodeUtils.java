package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.tree.NodeI;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import java.util.Iterator;

/**
 *
 * @author patrick
 */
public class NodeUtils {

    private NodeUtils() {
    }

    public static TLongList getSeqIDs(MGXMasterI master, NodeI<Long> node) throws MGXException {
        Iterator<Long> it = master.Sequence().fetchSequenceIDs(node.getAttribute());
        TLongList ids = new TLongArrayList(node.getContent().intValue());
        while (it.hasNext()) {
            ids.add(it.next());
        }
        return ids;
    }
}
