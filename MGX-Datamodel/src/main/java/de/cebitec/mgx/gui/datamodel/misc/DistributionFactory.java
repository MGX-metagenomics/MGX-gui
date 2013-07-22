package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.MGXMasterI;
import de.cebitec.mgx.gui.datamodel.tree.Node;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class DistributionFactory {

    public static Distribution merge(final Iterable<Distribution> dists) {
        Map<Attribute, Number> summary = new HashMap<>();
        long total = 0;
        MGXMasterI anyMaster = null;

        for (Distribution d : dists) {
            anyMaster = d.getMaster();
            total += d.getTotalClassifiedElements();
            for (Entry<Attribute, ? extends Number> e : d.entrySet()) {
                Attribute attr = e.getKey();
                long count = e.getValue().longValue();
                if (summary.containsKey(attr)) {
                    count += summary.get(attr).longValue();
                }
                summary.put(attr, count);
            }
        }

        return new Distribution(summary, total, anyMaster);
    }

    public static <T extends Number> Distribution fromTree(Tree<T> tree, AttributeType aType) {
        Map<Attribute, Number> summary = new HashMap<>();
        long total = 0;
        MGXMasterI master = aType.getMaster();
        
        for (Node<T> node : tree.getNodes()) {
            if (node.getAttribute().getAttributeType().getName().equals(aType.getName())) {
                summary.put(node.getAttribute(), node.getContent());
                total += node.getContent().longValue();
            }
        }
        return new Distribution(summary, total, master);
    }
}
