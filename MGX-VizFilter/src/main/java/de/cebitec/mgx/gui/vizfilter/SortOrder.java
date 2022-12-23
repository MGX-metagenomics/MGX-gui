package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class SortOrder<T extends Number> implements VisFilterI<DistributionI<T>, DistributionI<T>> {

    public final static int BY_VALUE = 0;
    public final static int BY_TYPE = 1;

    public static enum Order {

        ASCENDING, DESCENDING;
    }
    public final static Order ASCENDING = Order.ASCENDING;
    public final static Order DESCENDING = Order.DESCENDING;

    private final Order order;

    public SortOrder(Order order) {
        this.order = order;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Pair<GroupI, DistributionI<T>>> filter(List<Pair<GroupI, DistributionI<T>>> dists) {

        List<Pair<GroupI, DistributionI<T>>> ret = new ArrayList<>();
        AttributeTypeI attrType = null;

        // summary distribution over all groups
        TObjectDoubleMap<AttributeI> summary = new TObjectDoubleHashMap<>();
        for (Pair<GroupI, DistributionI<T>> pair : dists) {
            DistributionI<T> dist = pair.getSecond();
            attrType = dist.getAttributeType();
            for (Entry<AttributeI, T> p : dist.entrySet()) {
                if (summary.containsKey(p.getKey())) {
                    Double old = summary.get(p.getKey());
                    summary.put(p.getKey(), old + p.getValue().doubleValue());
                } else {
                    summary.put(p.getKey(), p.getValue().doubleValue());
                }
            }
        }

        List<AttributeI> sortList = new ArrayList<>();

        // if all distributions are empty, we have no attribute type
        int currentCriteria;
        if (attrType == null) {
            currentCriteria = SortOrder.BY_VALUE;
        } else {
            currentCriteria = attrType.getValueType() == AttributeTypeI.VALUE_NUMERIC
                    ? SortOrder.BY_TYPE : SortOrder.BY_VALUE;
        }

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

        for (Pair<GroupI, DistributionI<T>> p : dists) {
            DistributionI<T> d = p.getSecond();

            DistributionI<T> sortedDist = null;
            if (d.getEntryType().equals(Long.class)) {
                Map<AttributeI, Long> tmp = (Map<AttributeI, Long>) d;
                sortedDist = (DistributionI<T>) new Distribution(d.getMaster(), d.getAttributeType(), tmp, sortList, d.getTotalClassifiedElements());
            } else if (d.getEntryType().equals(Double.class)) {
                Map<AttributeI, Double> tmp = (Map<AttributeI, Double>) d;
                sortedDist = (DistributionI<T>) new NormalizedDistribution(d.getMaster(), d.getAttributeType(), tmp, sortList, d.getTotalClassifiedElements());
            }

            ret.add(new Pair<>(p.getFirst(), sortedDist));
        }

        return ret;
    }

    @SuppressWarnings("unchecked")
    public DistributionI<T> filterDist(DistributionI<T> d) {
        TObjectDoubleMap<AttributeI> summary = new TObjectDoubleHashMap<>();
        for (Entry<AttributeI, T> p : d.entrySet()) {
            if (summary.containsKey(p.getKey())) {
                Double old = summary.get(p.getKey());
                summary.put(p.getKey(), old + p.getValue().doubleValue());
            } else {
                summary.put(p.getKey(), p.getValue().doubleValue());
            }
        }

        List<AttributeI> sortList = new ArrayList<>();

        int currentCriteria = d.getAttributeType().getValueType() == AttributeTypeI.VALUE_NUMERIC
                ? SortOrder.BY_TYPE : SortOrder.BY_VALUE;

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
            sortedDist = (DistributionI<T>) new Distribution(d.getMaster(), d.getAttributeType(), tmp, sortList, d.getTotalClassifiedElements());
        } else if (d.getEntryType().equals(Double.class)) {
            Map<AttributeI, Double> tmp = (Map<AttributeI, Double>) d;
            sortedDist = (DistributionI<T>) new NormalizedDistribution(d.getMaster(), d.getAttributeType(), tmp, sortList, d.getTotalClassifiedElements());
        }
        return sortedDist;
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

        private final TObjectDoubleMap<AttributeI> base;

        public SortByValue(TObjectDoubleMap<AttributeI> base) {
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
