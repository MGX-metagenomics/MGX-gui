package de.cebitec.mgx.gui.datafactories;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * @author sjaenick
 */
public class DistributionFactory {

    public static <T> DistributionI<Long> merge(final Iterable<Future<Pair<T, DistributionI<Long>>>> dists, Map<T, DistributionI<Long>> cache) throws InterruptedException, ExecutionException {
        Map<AttributeI, Long> summary = new HashMap<>();
        long total = 0;
        MGXMasterI anyMaster = null;
        AttributeTypeI attrType = null;

        for (Future<Pair<T, DistributionI<Long>>> f : dists) {
            Pair<T, DistributionI<Long>> p = f.get();
            cache.put(p.getFirst(), p.getSecond());
            DistributionI<Long> d = p.getSecond();

            anyMaster = d.getMaster();
            attrType = d.getAttributeType();
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

        return new Distribution(anyMaster, attrType, summary, total);
    }

    public static Pair<DistributionI<Double>, DistributionI<Double>> statisticalMerge(final Iterable<DistributionI<Long>> dists) throws InterruptedException, ExecutionException {
        Map<AttributeI, StatisticsCalculator> summary = new HashMap<>();
        Map<AttributeI, Double> mean = new HashMap<>();
        Map<AttributeI, Double> stdv = new HashMap<>();
        long total = 0;
        long iterableSize = 0;
        MGXMasterI anyMaster = null;
        AttributeTypeI attrType = null;

        for (DistributionI<Long> d : dists) {
            anyMaster = d.getMaster();
            attrType = d.getAttributeType();
            total += d.getTotalClassifiedElements();
            iterableSize++;
            for (Entry<AttributeI, Long> e : d.entrySet()) {
                AttributeI attr = e.getKey();
                long count = e.getValue();
                if (!summary.containsKey(attr)) {
                    summary.put(attr, new StatisticsCalculator());
                }
                summary.get(attr).add(count);
            }
        }
        for (Entry<AttributeI, StatisticsCalculator> entry : summary.entrySet()) {
            for (long i = entry.getValue().count(); i < iterableSize; i++) {
                entry.getValue().add(0);
            }

            mean.put(entry.getKey(), entry.getValue().getMean());
            stdv.put(entry.getKey(), entry.getValue().getStdv());
        }

        return new Pair<>(new NormalizedDistribution(anyMaster, attrType, mean, total), new NormalizedDistribution(anyMaster, attrType, stdv, total));
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
        return new Distribution(master, aType, summary, total);
    }

    public static <T extends Long> Map<String, DistributionI<Long>> splitTree(TreeI<T> tree) {
        Map<String, DistributionI<Long>> ret = new HashMap<>();
        Map<AttributeTypeI, Map<AttributeI, Long>> temp = new HashMap<>();
        
        for (NodeI<T> node : tree.getNodes()) {
            AttributeI attr = node.getAttribute();
            AttributeTypeI aType = attr.getAttributeType();
            T count = node.getContent();
            if (!temp.containsKey(aType)) {
                temp.put(aType, new HashMap<>());
            }
            temp.get(aType).put(attr, count);
        }
        
        for (Entry<AttributeTypeI, Map<AttributeI, Long>> e : temp.entrySet()) {
            AttributeTypeI aType = e.getKey();
            long total = 0;
            for (Long l : e.getValue().values()) {
                total += l;
            }
            ret.put(aType.getName(), new Distribution(aType.getMaster(), aType, e.getValue(), total));
        }
        return ret;
    }

    private static class StatisticsCalculator {

        private long n;
        private double mean, m2;

        public StatisticsCalculator() {
            n = 0;
            mean = 0;
            m2 = 0;
        }

        public void add(double value) {
            n++;
            double delta = value - mean;
            mean += delta / n;
            m2 += delta * (value - mean);
        }

        public double getMean() {
            return mean;
        }

        public double getStdv() {
            if (n < 2) {
                return Double.NaN;
            } else {
                return FastMath.sqrt(m2 / (n - 1));
            }
        }

        public long count() {
            return n;
        }

        public long getCount() {
            return n;
        }
    }
}
