package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.visualization.filter.ReplicateVisFilterI;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class ReplicateSortOrder<T extends Number> implements ReplicateVisFilterI<DistributionI<T>, DistributionI<T>> {

    public final static int BY_VALUE = 0;
    public final static int BY_TYPE = 1;

    public static enum Order {

        ASCENDING, DESCENDING;
    }
    public final static Order ASCENDING = Order.ASCENDING;
    public final static Order DESCENDING = Order.DESCENDING;

    private final int currentCriteria;
    private final Order order;

    private final List<AttributeI> sortList = new ArrayList<>();

    public ReplicateSortOrder(AttributeTypeI aType, Order order) {
        if (aType == null) {
            throw new IllegalArgumentException("null AttributeTypeI supplied, but value is required");
        }
        currentCriteria = aType.getValueType() == AttributeTypeI.VALUE_NUMERIC
                ? ReplicateSortOrder.BY_TYPE : ReplicateSortOrder.BY_VALUE;
        this.order = order;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Triple<ReplicateGroupI, DistributionI<T>, DistributionI<T>>> filter(List<Triple<ReplicateGroupI, DistributionI<T>, DistributionI<T>>> dists) {

        List<Triple<ReplicateGroupI, DistributionI<T>, DistributionI<T>>> ret = new ArrayList<>();

        // summary distribution over all groups
        Map<AttributeI, Double> summary = new HashMap<>();
        for (Triple<ReplicateGroupI, DistributionI<T>, DistributionI<T>> trip : dists) {
            DistributionI<T> mean = trip.getSecond();
            DistributionI<T> stdv = trip.getThird();
            for (Entry<AttributeI, T> p : mean.entrySet()) {
                if (summary.containsKey(p.getKey())) {
                    Double old = summary.get(p.getKey());
                    summary.put(p.getKey(), old + p.getValue().doubleValue());
                } else {
                    summary.put(p.getKey(), p.getValue().doubleValue());
                }
            }
        }

        sortList.clear();
        // sort by selected criteria
        switch (currentCriteria) {
            case BY_VALUE:
                sortList.addAll(summary.keySet());
                Collections.sort(sortList, new SortByValue(summary));
                break;
            case BY_TYPE:
                sortList.addAll(summary.keySet());
                Collections.sort(sortList, new SortNumerically());
                break;
            default:
                assert (false);
                break;
        }

        assert summary.size() == sortList.size();

        if (order == Order.ASCENDING) {
            Collections.reverse(sortList);
        }

        for (Triple<ReplicateGroupI, DistributionI<T>, DistributionI<T>> t : dists) {
            DistributionI<T> mean = t.getSecond();
            DistributionI<T> stdv = t.getThird();

            Map<AttributeI, Double> tmpMean = (Map<AttributeI, Double>) mean;
            Map<AttributeI, Double> tmpStdv = (Map<AttributeI, Double>) stdv;
            DistributionI<T> sortedMean = (DistributionI<T>) new NormalizedDistribution(mean.getMaster(), tmpMean, sortList, mean.getTotalClassifiedElements());
            DistributionI<T> sortedStdv = (DistributionI<T>) new NormalizedDistribution(stdv.getMaster(), tmpStdv, sortList, stdv.getTotalClassifiedElements());
            

            ret.add(new Triple<>(t.getFirst(), sortedMean, sortedStdv));
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    public DistributionI<T> filterDist(DistributionI<T> d) {
        Map<AttributeI, Double> summary = new HashMap<>();
        for (Entry<AttributeI, T> p : d.entrySet()) {
            if (summary.containsKey(p.getKey())) {
                Double old = summary.get(p.getKey());
                summary.put(p.getKey(), old + p.getValue().doubleValue());
            } else {
                summary.put(p.getKey(), p.getValue().doubleValue());
            }
        }

        sortList.clear();
        // sort by selected criteria
        switch (currentCriteria) {
            case BY_VALUE:
                sortList.addAll(summary.keySet());
                Collections.sort(sortList, new SortByValue(summary));
                break;
            case BY_TYPE:
                sortList.addAll(summary.keySet());
                Collections.sort(sortList, new SortNumerically());
                break;
            default:
                assert (false);
                break;
        }

        assert summary.size() == sortList.size();

        if (order == Order.ASCENDING) {
            Collections.reverse(sortList);
        }

        DistributionI<T> sortedDist = null;
        if (d.getEntryType().equals(Long.class)) {
            Map<AttributeI, Long> tmp = (Map<AttributeI, Long>) d;
            sortedDist = (DistributionI<T>) new Distribution(d.getMaster(), tmp, sortList, d.getTotalClassifiedElements());
        } else if (d.getEntryType().equals(Double.class)) {
            Map<AttributeI, Double> tmp = (Map<AttributeI, Double>) d;
            sortedDist = (DistributionI<T>) new NormalizedDistribution(d.getMaster(), tmp, sortList, d.getTotalClassifiedElements());
        }
        return sortedDist;
    }

    public List<AttributeI> getOrder() {
        return sortList;
    }

    public final static class SortNumerically implements Comparator<AttributeI> {

        @Override
        public int compare(AttributeI a1, AttributeI a2) {
            Double d1 = Double.parseDouble(a1.getValue());
            Double d2 = Double.parseDouble(a2.getValue());
            return d2.compareTo(d1);
        }
    }

    public final static class SortByValue implements Comparator<AttributeI> {

        private final Map<AttributeI, Double> base;

        public SortByValue(Map<AttributeI, Double> base) {
            this.base = base;
        }

        @Override
        public int compare(AttributeI a, AttributeI b) {
            Double d1 = base.get(a);
            Double d2 = base.get(b);
            return d2.compareTo(d1);
        }
    }
}
