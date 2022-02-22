package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.visualization.filter.ReplicateVisFilterI;
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
 * @author pblumenk
 */
public class ReplicateSortOrder implements ReplicateVisFilterI<DistributionI<Double>, DistributionI<Double>> {

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
    public List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> filter(List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> dists) {

        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> ret = new ArrayList<>();

        // summary distribution over all groups
        Map<AttributeI, Double> summary = new HashMap<>();
        for (Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>> trip : dists) {
            DistributionI<Double> mean = trip.getSecond();
            DistributionI<Double> stdv = trip.getThird();
            for (Entry<AttributeI, Double> p : mean.entrySet()) {
                if (summary.containsKey(p.getKey())) {
                    Double old = summary.get(p.getKey());
                    summary.put(p.getKey(), old + p.getValue());
                } else {
                    summary.put(p.getKey(), p.getValue());
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

        for (Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>> t : dists) {
            DistributionI<Double> mean = t.getSecond();
            DistributionI<Double> stdv = t.getThird();

            Map<AttributeI, Double> tmpMean = (Map<AttributeI, Double>) mean;
            Map<AttributeI, Double> tmpStdv = (Map<AttributeI, Double>) stdv;
            DistributionI<Double> sortedMean = (DistributionI<Double>) new NormalizedDistribution(mean.getMaster(), mean.getAttributeType(), tmpMean, sortList, mean.getTotalClassifiedElements());
            DistributionI<Double> sortedStdv = (DistributionI<Double>) new NormalizedDistribution(stdv.getMaster(), stdv.getAttributeType(), tmpStdv, sortList, stdv.getTotalClassifiedElements());

            ret.add(new Triple<>(t.getFirst(), sortedMean, sortedStdv));
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    public DistributionI<Double> filterDist(DistributionI<Double> d) {
        Map<AttributeI, Double> summary = new HashMap<>();
        for (Entry<AttributeI, Double> p : d.entrySet()) {
            if (summary.containsKey(p.getKey())) {
                Double old = summary.get(p.getKey());
                summary.put(p.getKey(), old + p.getValue());
            } else {
                summary.put(p.getKey(), p.getValue());
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

        Map<AttributeI, Double> tmp = (Map<AttributeI, Double>) d;
        DistributionI<Double> sortedDist = (DistributionI<Double>) new NormalizedDistribution(d.getMaster(), d.getAttributeType(), tmp, sortList, d.getTotalClassifiedElements());
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
