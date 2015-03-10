package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class ToFractionFilter implements VisFilterI<DistributionI<Long>, DistributionI<Double>> {

    @Override
    public List<Pair<VisualizationGroupI, DistributionI<Double>>> filter(List<Pair<VisualizationGroupI, DistributionI<Long>>> dists) {
        List<Pair<VisualizationGroupI, DistributionI<Double>>> ret = new ArrayList<>(dists.size());
        for (Pair<VisualizationGroupI, DistributionI<Long>> pair : dists) {
            ret.add(new Pair<>(pair.getFirst(), convertSingleDistribution(pair.getSecond())));
        }
        return ret;
    }

    private DistributionI<Double> convertSingleDistribution(DistributionI<Long> dist) {

        // sum up
        long total = 0;
        for (Long n : dist.values()) {
            total += n;
        }
        //long total = dist.getTotalClassifiedElements();
        Map<AttributeI, Double> map = new HashMap<>(dist.size());
        
        for (Entry<AttributeI, Long> e : dist.entrySet()) {
            //e.setValue((double)e.getValue().longValue() / total);
            map.put(e.getKey(), (double)e.getValue() / total);
        }
        
        return new NormalizedDistribution(dist.getMaster(), map);
    }
}
