package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author sjaenick
 */
public class SortOrder implements VisFilterI<Distribution> {

    public final static String BY_VALUE = "Value";
    public final static String BY_TYPE = "Type";
    private String currentCriteria = BY_VALUE;
    
    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {

        // summary distribution over all groups
        Map<Attribute, Long> summary = new HashMap<>();
        for (Pair<VisualizationGroup, Distribution> pair : dists) {
            for (Pair<Attribute, ? extends Number> e : pair.getSecond().getSorted()) {
                if (summary.containsKey(e.getFirst())) {
                    Long get = summary.get(e.getFirst());
                    get += e.getSecond().longValue();
                    summary.put(e.getFirst(), get);
                } else {
                    summary.put(e.getFirst(), e.getSecond().longValue());
                }
            }
        }

        List<Attribute> sortList = new ArrayList<>();
        // sort by selected criteria
        switch (currentCriteria) {
            case BY_VALUE:
                // we use a TreeMap to sort by abundance, descending
                SortByValue bvc = new SortByValue(summary);
                TreeMap<Attribute, Long> sorted_map = new TreeMap<>(bvc);
                sorted_map.putAll(summary);
                sortList.addAll(sorted_map.keySet());
                break;
            case BY_TYPE:
                sortList.addAll(summary.keySet());
                Collections.sort(sortList, new SortNumerically());
                break;
            default:
                assert(false);
                break;
        }
        
        for (Pair<VisualizationGroup, Distribution> p : dists) {
            p.getSecond().setSortOrder(sortList.toArray(new Attribute[0]));
        }

        return dists;
    }
    
    public void setSortCriteria(String criteria) {
        currentCriteria = criteria;
    }

    private final static class SortNumerically implements Comparator<Attribute>, Serializable {

        @Override
        public int compare(Attribute a1, Attribute a2) {
            Double d1 = Double.parseDouble(a1.getValue());
            Double d2 = Double.parseDouble(a2.getValue());
            if (d1 < d2) {
                return 1;
            } else if (d1.doubleValue() == d2.doubleValue()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    private final static class SortByValue implements Comparator<Attribute> {

        Map<Attribute, Long> base;

        public SortByValue(Map<Attribute, Long> base) {
            this.base = base;
        }

        @Override
        public int compare(Attribute a, Attribute b) {
            if (base.get(a).longValue() < base.get(b).longValue()) {
                return 1;
            } else if (base.get(a).longValue() == base.get(b).longValue()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
