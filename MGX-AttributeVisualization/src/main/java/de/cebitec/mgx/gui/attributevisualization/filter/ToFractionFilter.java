package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.attributevisualization.Pair;
import de.cebitec.mgx.gui.attributevisualization.data.Distribution;
import de.cebitec.mgx.gui.attributevisualization.data.VisualizationGroup;
import de.cebitec.mgx.gui.datamodel.Attribute;
import java.util.*;

/**
 *
 * @author sjaenick
 */
public class ToFractionFilter implements VisFilterI {

    @Override
    public List<Pair<VisualizationGroup, Distribution>> filter(List<Pair<VisualizationGroup, Distribution>> dists) {
        System.err.println("toFraction.filter()");
        List<Pair<VisualizationGroup, Distribution>> ret = new ArrayList<Pair<VisualizationGroup, Distribution>>();
        for (Pair<VisualizationGroup, Distribution> pair : dists) {
            VisualizationGroup vg = pair.getFirst();
            Distribution dist = pair.getSecond();
            ret.add(new Pair<VisualizationGroup, Distribution>(vg, convertSingleDistribution(dist)));
        }
        return ret;
    }

    private Distribution convertSingleDistribution(Distribution dist) {

        // sum up
        long sum = 0;
        for (Pair<Attribute, Number> p : dist.get()) {
            sum += p.getSecond().longValue();
        }
        assert sum > 0;

        Map<Attribute, Number> tmp = new HashMap<Attribute, Number>();
        for (Pair<Attribute, Number> p : dist.get()) {
            tmp.put(p.getFirst(), (double)p.getSecond().longValue() / sum);
        }
        Distribution d =  new Distribution(tmp);
        d.setSortOrder(dist.getSortOrder());
        return d;
    }
}
