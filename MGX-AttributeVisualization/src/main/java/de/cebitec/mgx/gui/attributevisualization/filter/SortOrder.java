package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class SortOrder implements VisFilterI<Distribution> {

    public final static int BY_VALUE = 0;
    public final static int BY_TYPE = 1;
    
    public final static int ASCENDING = 0;
    public final static int DESCENDING = 1;
    
    private final int currentCriteria;
    private final int order;

    public SortOrder(AttributeType aType, int order) {
        assert aType != null;
        currentCriteria = aType.getValueType() == AttributeType.VALUE_NUMERIC
                ? SortOrder.BY_TYPE : SortOrder.BY_VALUE;
        this.order = order;
    }

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {

        // summary distribution over all groups
        Map<Attribute, Double> summary = new HashMap<>();
        for (Pair<VisualizationGroup, Distribution> pair : dists) {
//            for (Attribute a : pair.getSecond().getSortOrder()) {
//                if (summary.containsKey(a)) {
//                    Double old = summary.get(a);
//                    summary.put(a, old + pair.getSecond().getMap().get(a).doubleValue());
//                } else {
//                    summary.put(a, pair.getSecond().getMap().get(a).doubleValue());
//                }
//            }
            for (Entry<Attribute, ? extends Number> p : pair.getSecond().entrySet()) {
                if (summary.containsKey(p.getKey())) {
                    Double old = summary.get(p.getKey());
                    summary.put(p.getKey(), old + p.getValue().doubleValue());
                } else {
                    summary.put(p.getKey(), p.getValue().doubleValue());
                }
            }

//            }
        }


        List<Attribute> sortList = new ArrayList<>();
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
        
        if (order == ASCENDING) {
            Collections.reverse(sortList);
        }

        for (Pair<VisualizationGroup, Distribution> p : dists) {
            Distribution d = p.getSecond();

            // set the sort order
            d.setOrder(sortList);
        }

        return dists;
    }

    public final static class SortNumerically implements Comparator<Attribute>, Serializable {

        @Override
        public int compare(Attribute a1, Attribute a2) {
            Double d1 = Double.parseDouble(a1.getValue());
            Double d2 = Double.parseDouble(a2.getValue());
            return d2.compareTo(d1);
        }
    }

    public final static class SortByValue implements Comparator<Attribute> {

        Map<Attribute, Double> base;

        public SortByValue(Map<Attribute, Double> base) {
            this.base = base;
        }

        @Override
        public int compare(Attribute a, Attribute b) {
            Double d1 = base.get(a);
            Double d2 = base.get(b);
            return d2.compareTo(d1);
        }
    }
}
