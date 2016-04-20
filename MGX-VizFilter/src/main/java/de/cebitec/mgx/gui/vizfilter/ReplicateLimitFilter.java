package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
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
public class ReplicateLimitFilter<T extends Number> implements VisFilterI<DistributionI<T>, DistributionI<T>> {

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
    public List<Pair<VisualizationGroupI, DistributionI<T>>> filter(List<Pair<VisualizationGroupI, DistributionI<T>>> dists) {

        List<Pair<VisualizationGroupI, DistributionI<T>>> ret = new ArrayList<>();

//        if (limit == LIMITS.ALL) {
//            return dists;
//        }
        // merge distributions
        Map<AttributeI, Double> summary = new HashMap<>();
        for (Pair<VisualizationGroupI, DistributionI<T>> pair : dists) {
            for (Map.Entry<AttributeI, T> e : pair.getSecond().entrySet()) {
                if (summary.containsKey(e.getKey())) {
                    Double old = summary.get(e.getKey());
                    summary.put(e.getKey(), old + e.getValue().doubleValue());
                } else {
                    summary.put(e.getKey(), e.getValue().doubleValue());
                }
            }
        }

        // find most abundant entries
        List<AttributeI> sortList = new ArrayList<>();
        sortList.addAll(summary.keySet());
        Collections.sort(sortList, new SortOrder.SortByValue(summary));

        List<AttributeI> toKeep = null;
        if (limit.equals(LIMITS.ALL)) {
            toKeep = sortList;
        } else {
            toKeep = sortList.size() > limit.getValue()
                    ? sortList.subList(0, limit.getValue())
                    : sortList;
        }

        for (Pair<VisualizationGroupI, DistributionI<T>> p : dists) {
            DistributionI<T> dist = p.getSecond();

            DistributionI<T> filteredDist = null;
            if (dist.getEntryType().equals(Long.class)) {
                Map<AttributeI, Long> tmp = (Map<AttributeI, Long>) dist;
                filteredDist = (DistributionI<T>) new Distribution(dist.getMaster(), tmp, toKeep, dist.getTotalClassifiedElements());
            } else if (dist.getEntryType().equals(Double.class)) {
                Map<AttributeI, Double> tmp = (Map<AttributeI, Double>) dist;
                filteredDist = (DistributionI<T>) new NormalizedDistribution(dist.getMaster(), tmp, toKeep, dist.getTotalClassifiedElements());
            }

            ret.add(new Pair<>(p.getFirst(), filteredDist));
        }

        return ret;
    }
}
