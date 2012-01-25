package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.attributevisualization.Pair;
import de.cebitec.mgx.gui.attributevisualization.data.Distribution;
import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
import de.cebitec.mgx.gui.datamodel.Attribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author sjaenick
 */
public class SortOrder implements VisFilterI {

    public final static String BY_VALUE = "Value";
    public final static String BY_TYPE = "Type";
    private String currentCriteria = BY_VALUE;
    //
    public final static String ASCENDING = "ascending";
    public final static String DESCENDING = "descending";
    private String currentOrder = DESCENDING;
    
    
    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {

        System.err.println("SortOrder.filter()");
        // summary distribution over all groups
        Map<Attribute, Long> summary = new HashMap<Attribute, Long>();
        for (Pair<VisualizationGroup, Distribution> pair : dists) {
            for (Pair<Attribute, ? extends Number> e : pair.getSecond().get()) {
                if (summary.containsKey(e.getFirst())) {
                    Long get = summary.get(e.getFirst());
                    get += e.getSecond().longValue();
                    summary.put(e.getFirst(), get);
                } else {
                    summary.put(e.getFirst(), e.getSecond().longValue());
                }
            }
        }

        List<Attribute> sortList = new ArrayList<Attribute>();

        // sort by selected criteria
        if ((currentCriteria.equals(BY_VALUE)) )  {
            // we use a TreeMap to sort by abundance, descending
            SortByValue bvc = new SortByValue(summary);
            TreeMap<Attribute, Long> sorted_map = new TreeMap<Attribute, Long>(bvc);
            sorted_map.putAll(summary);

            sortList.addAll(sorted_map.keySet());

        } else if (currentCriteria.equals(BY_TYPE)) {
            sortList.addAll(summary.keySet());
            Collections.sort(sortList, new SortNumerically());
        } else {
            assert(false);
        }
        
        // sort ascending/descending
        if (currentOrder.equals(ASCENDING)) {
            Collections.reverse(sortList);
        } else if (currentOrder.equals(DESCENDING)) {
            
        } else {
            assert(false);
        }

        for (Pair<VisualizationGroup, Distribution> p : dists) {
            p.getSecond().setSortOrder(sortList);
        }

        return dists;
    }
    
    public void setSortCriteria(String criteria) {
        currentCriteria = criteria;
    }
    
    public void setSortOrder(String order) {
        currentOrder = order;
    }

    private final class SortNumerically implements Comparator<Attribute> {

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

    private final class SortByValue implements Comparator<Attribute> {

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
