package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.*;

/**
 *
 * @author sjaenick
 */
public class LimitFilter implements VisFilterI<Distribution> {

    private LIMITS limit = LIMITS.ALL;

    public enum LIMITS {

        ALL("All", -1),
        TOP10("Top 10", 10),
        TOP25("Top 25", 25),
        TOP50("Top 50", 50),
        TOP100("Top 100", 100);
        private String s;
        private int l;

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

    public void setLimit(LIMITS max) {
        limit = max;
    }

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {
        if (limit == LIMITS.ALL) {
            return dists;
        }

        // summary distribution over all groups
        Map<Attribute, Double> summary = new HashMap<>();
        for (Pair<VisualizationGroup, Distribution> pair : dists) {
            for (Map.Entry<Attribute, ? extends Number> e : pair.getSecond().entrySet()) {
                if (summary.containsKey(e.getKey())) {
                    Double old = summary.get(e.getKey());
                    summary.put(e.getKey(), old + e.getValue().doubleValue());
                } else {
                    summary.put(e.getKey(), e.getValue().doubleValue());
                }
            }
        }
        
        // find most abundant entries
        List<Attribute> sortList = new ArrayList<>();
        sortList.addAll(summary.keySet());
        Collections.sort(sortList, new SortOrder.SortByValue(summary));

        List<Attribute> toKeep = sortList.size() > limit.getValue() 
                ? sortList.subList(0, limit.getValue())
                : sortList;
        
        
        for (Pair<VisualizationGroup, Distribution> p : dists) {
            p.getSecond().setOrder(toKeep);
        }

        return dists;
    }
}
