package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Distribution;
import de.cebitec.mgx.gui.datamodel.Pair;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class ToFractionFilter implements VisFilterI<Distribution> {

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {
        List<Pair<VisualizationGroup, Distribution>> ret = new ArrayList<>();
        for (Pair<VisualizationGroup, Distribution> pair : dists) {
            VisualizationGroup vg = pair.getFirst();
            Distribution dist = pair.getSecond();
            ret.add(new Pair<>(vg, convertSingleDistribution(dist)));
        }
        
        return ret;
    }

    private Distribution convertSingleDistribution(Distribution dist) {

        // sum up
        long sum = 0;
        for (Entry<Attribute, ? extends Number> p : dist.entrySet()) {
            sum += p.getValue().longValue();
        }

        for (Entry<Attribute, Number> e : dist.entrySet()) {
            e.setValue((double)e.getValue().longValue() / sum);
        }
        
        return dist;
    }
}
