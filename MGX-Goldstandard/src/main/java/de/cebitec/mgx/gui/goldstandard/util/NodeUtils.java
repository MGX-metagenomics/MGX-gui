package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author patrick
 */
public class NodeUtils {

    public static List<Long> getSeqIDs(NodeI<?> node) throws MGXException {
        Iterator<Long> it = node.getAttribute().getMaster().Sequence().fetchSequenceIDs(node.getAttribute());;
        List<Long> ids = new LinkedList<>();
        while (it.hasNext()) {
            ids.add(it.next());
        }
        return ids;
    }
}
