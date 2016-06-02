package de.cebitec.mgx.gui.goldstandard.util;

import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.gui.datamodel.tree.Node;
import java.util.List;

/**
 *
 * @author patrick
 */
public class NodeComparison {
    public static <T> Result compare(NodeI<T> a, NodeI<T> b){        
    }

    public static class Result<T> {

        public final List<Pair<AttributeI, T>> onlyA;
        public final List<Pair<AttributeI, T>> onlyB;
        public final List<Pair<AttributeI, T>> ab;
        
        public Result(List<Pair<AttributeI, T>> onlyA, ) {
            
        }
    }
}
