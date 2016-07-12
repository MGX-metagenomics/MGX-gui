package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.tree.NodeI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author patrick
 */
public class NodeUtils {

    private NodeUtils() {
    }

    public static List<Long> getSeqIDs(NodeI<Long> node) throws MGXException {
        Iterator<Long> it = node.getAttribute().getMaster().Sequence().fetchSequenceIDs(node.getAttribute());
        List<Long> ids = new ArrayList<>(node.getContent().intValue());
        while (it.hasNext()) {
            ids.add(it.next());
        }
        return ids;
    }
}
