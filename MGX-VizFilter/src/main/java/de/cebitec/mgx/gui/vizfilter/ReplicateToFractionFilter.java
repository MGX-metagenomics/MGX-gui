package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.visualization.filter.ReplicateVisFilterI;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author pblumenk
 */
public class ReplicateToFractionFilter implements ReplicateVisFilterI<DistributionI<Double>, DistributionI<Double>> {

    @Override
    public List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> filter(List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> dists) {
        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> ret = new ArrayList<>(dists.size());
        for (Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>> triple : dists) {
            Pair<DistributionI<Double>, DistributionI<Double>> statDists = filterDist(triple.getSecond(), triple.getThird());
            ret.add(new Triple<>(triple.getFirst(), statDists.getFirst(), statDists.getSecond()));
        }
        return ret;
    }

    public Pair<DistributionI<Double>, DistributionI<Double>> filterDist(DistributionI<Double> mean, DistributionI<Double> stdv) {
        // sum up
        double total = 0;
        for (double n : mean.values()) {
            total += n;
        }
        //long total = dist.getTotalClassifiedElements();
        Map<AttributeI, Double> meanRet = new HashMap<>(mean.size());
        Map<AttributeI, Double> stdvRet = new HashMap<>(stdv.size());

        for (Entry<AttributeI, Double> e : mean.entrySet()) {
            //e.setValue((double)e.getValue().longValue() / total);
            meanRet.put(e.getKey(), e.getValue() / total);
        }
        for (Entry<AttributeI, Double> e : stdv.entrySet()) {
            //e.setValue((double)e.getValue().longValue() / total);
            stdvRet.put(e.getKey(), e.getValue() / total);
        }

        return new Pair<>(
                new NormalizedDistribution(mean.getMaster(), mean.getAttributeType(), meanRet, mean.keySet(), mean.getTotalClassifiedElements()),
                new NormalizedDistribution(stdv.getMaster(), stdv.getAttributeType(), stdvRet, stdv.keySet(), stdv.getTotalClassifiedElements()));
    }
}
