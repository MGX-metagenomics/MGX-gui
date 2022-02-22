package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.visualization.filter.ReplicateVisFilterI;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class ReplicateLimitFilter implements ReplicateVisFilterI<DistributionI<Double>, DistributionI<Double>> {

    private final LIMITS limit;

    public enum LIMITS {

        ALL("All", -1),
        TOP10("Top 10", 10),
        TOP25("Top 25", 25),
        TOP50("Top 50", 50),
        TOP100("Top 100", 100);
        private final String s;
        private final int l;

        private LIMITS(String s, int c) {
            this.s = s;
            l = c;
        }

        public int getValue() {
            return l;
        }

        @Override
        public String toString() {
            return s;
        }

    };

    public ReplicateLimitFilter(LIMITS limit) {
        this.limit = limit;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> filter(List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> dists) {

        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> ret = new ArrayList<>();

//        if (limit == LIMITS.ALL) {
//            return dists;
//        }
        
        // merge distributions
        Map<AttributeI, Double> summary = new HashMap<>();
        for (Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>> triple : dists) {
            for (Map.Entry<AttributeI, Double> e : triple.getSecond().entrySet()) {
                if (summary.containsKey(e.getKey())) {
                    Double old = summary.get(e.getKey());
                    summary.put(e.getKey(), old + e.getValue());
                } else {
                    summary.put(e.getKey(), e.getValue());
                }
            }
        }
        
        // find most abundant entries
        List<AttributeI> sortList = new ArrayList<>();
        sortList.addAll(summary.keySet());
        Collections.sort(sortList, new ReplicateSortOrder.SortByValue(summary));

        List<AttributeI> toKeep = null;
        if (limit.equals(LIMITS.ALL)) {
            toKeep = sortList;
        } else {
            toKeep = sortList.size() > limit.getValue()
                    ? sortList.subList(0, limit.getValue())
                    : sortList;
        }

        for (Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>> t : dists) {
            DistributionI<Double> mean = t.getSecond();
            DistributionI<Double> stdv = t.getThird();

            Map<AttributeI, Double> tmpMean = (Map<AttributeI, Double>) mean;
            Map<AttributeI, Double> tmpStdv = (Map<AttributeI, Double>) stdv;
            DistributionI<Double> sortedMean = (DistributionI<Double>) new NormalizedDistribution(mean.getMaster(), mean.getAttributeType(), tmpMean, toKeep, mean.getTotalClassifiedElements());
            DistributionI<Double> sortedStdv = (DistributionI<Double>) new NormalizedDistribution(stdv.getMaster(), stdv.getAttributeType(), tmpStdv, toKeep, stdv.getTotalClassifiedElements());
            

            ret.add(new Triple<>(t.getFirst(), sortedMean, sortedStdv));
        }

        return ret;
    }
}
