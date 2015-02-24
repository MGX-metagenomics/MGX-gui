package de.cebitec.mgx.common;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author sjaenick
 */
public class DistributionFactory {

    public static DistributionI merge(final Iterable<Future<Pair<SeqRunI, DistributionI>>> dists, Map<SeqRunI, DistributionI> cache) throws InterruptedException, ExecutionException {
        Map<AttributeI, Number> summary = new HashMap<>();
        long total = 0;
        MGXMasterI anyMaster = null;

        for (Future<Pair<SeqRunI, DistributionI>> f : dists) {
            Pair<SeqRunI, DistributionI> p = f.get();
            cache.put(p.getFirst(), p.getSecond());
            DistributionI d = p.getSecond();
            
            anyMaster = d.getMaster();
            total += d.getTotalClassifiedElements();
            for (Entry<AttributeI, Number> e : d.entrySet()) {
                AttributeI attr = e.getKey();
                long count = e.getValue().longValue();
                if (summary.containsKey(attr)) {
                    count += summary.get(attr).longValue();
                }
                summary.put(attr, count);
            }
        }

        return new Distribution(summary, total, anyMaster);
    }

    public static <T extends Number> DistributionI fromTree(TreeI<T> tree, AttributeTypeI aType) {
        Map<AttributeI, Number> summary = new HashMap<>();
        long total = 0;
        MGXMasterI master = aType.getMaster();
        
        for (NodeI<T> node : tree.getNodes()) {
            if (node.getAttribute().getAttributeType().getName().equals(aType.getName())) {
                summary.put(node.getAttribute(), node.getContent());
                total += node.getContent().longValue();
            }
        }
        return new Distribution(summary, total, master);
    }
}
