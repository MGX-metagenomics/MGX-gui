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

    public static DistributionI<Long> merge(final Iterable<Future<Pair<SeqRunI, DistributionI<Long>>>> dists, Map<SeqRunI, DistributionI<Long>> cache) throws InterruptedException, ExecutionException {
        Map<AttributeI, Long> summary = new HashMap<>();
        long total = 0;
        MGXMasterI anyMaster = null;

        for (Future<Pair<SeqRunI, DistributionI<Long>>> f : dists) {
            Pair<SeqRunI, DistributionI<Long>> p = f.get();
            cache.put(p.getFirst(), p.getSecond());
            DistributionI<Long> d = p.getSecond();

            anyMaster = d.getMaster();
            total += d.getTotalClassifiedElements();
            for (Entry<AttributeI, Long> e : d.entrySet()) {
                AttributeI attr = e.getKey();
                long count = e.getValue();
                if (summary.containsKey(attr)) {
                    count += summary.get(attr);
                }
                summary.put(attr, count);
            }
        }

        return new Distribution(anyMaster, summary, total);
    }

    public static <T extends Long> DistributionI<Long> fromTree(TreeI<T> tree, AttributeTypeI aType) {
        Map<AttributeI, Long> summary = new HashMap<>();
        MGXMasterI master = aType.getMaster();
        long total = 0;

        for (NodeI<T> node : tree.getNodes()) {
            if (node.getAttribute().getAttributeType().getName().equals(aType.getName())) {
                summary.put(node.getAttribute(), node.getContent());
                total += node.getContent();
            }
        }
        return new Distribution(master, summary, total);
    }
}
