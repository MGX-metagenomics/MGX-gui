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
public class AbundanceSortFilter implements VisFilter {

    public final static int ASCENDING = 1;
    public final static int DESCENDING = 2;
    private int sortOrder = DESCENDING;

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {

        System.err.println("abSort.filter()");
        // summary distribution over all groups
        Map<Attribute, Long> summary = new HashMap<Attribute, Long>();
        for (Pair<VisualizationGroup, Distribution> pair : dists) {
            for (Pair<Attribute, Number> e : pair.getSecond().get()) {
                if (summary.containsKey(e.getFirst())) {
                    Long get = summary.get(e.getFirst());
                    get += e.getSecond().longValue();
                    summary.put(e.getFirst(), get);
                } else {
                    summary.put(e.getFirst(), e.getSecond().longValue());
                }
            }
        }

        // we use a TreeMap to sort by abundance
        ValueComparator bvc = new ValueComparator(summary);
        TreeMap<Attribute, Long> sorted_map = new TreeMap<Attribute, Long>(bvc);
        sorted_map.putAll(summary);

        List<Attribute> sorted = new ArrayList<Attribute>();
        sorted.addAll(sorted_map.keySet());

        if (sortOrder == DESCENDING) {
        } else if (sortOrder == ASCENDING) {
            Collections.reverse(sorted);
        }

        for (Pair<VisualizationGroup, Distribution> p : dists) {
            p.getSecond().setSortOrder(sorted);
        }

        return dists;
    }

    private final class ValueComparator implements Comparator<Attribute> {

        Map<Attribute, Long> base;

        public ValueComparator(Map<Attribute, Long> base) {
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
