package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.visualization.filter.ReplicateVisFilterI;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author sj
 */
public class ReplicateExcludeFilter<T extends Number> implements ReplicateVisFilterI<DistributionI<T>, DistributionI<T>> {

    private final Set<AttributeI> blacklist;

    public ReplicateExcludeFilter(Set<AttributeI> blacklist) {
        if (blacklist == null) {
           throw new IllegalArgumentException();
        }
        this.blacklist = Collections.unmodifiableSet(blacklist);
    }

    @Override
    public List<Triple<ReplicateGroupI, DistributionI<T>, DistributionI<T>>> filter(List<Triple<ReplicateGroupI, DistributionI<T>, DistributionI<T>>> in) {
        List<Triple<ReplicateGroupI, DistributionI<T>, DistributionI<T>>> ret = new ArrayList<>(in.size());
        for (Triple<ReplicateGroupI, DistributionI<T>, DistributionI<T>> t : in) {
            DistributionI<T> filteredMeanDist = filterDist(t.getSecond());
            DistributionI<T> filteredStdvDist = filterDist(t.getThird());
            ret.add(new Triple<>(t.getFirst(), filteredMeanDist, filteredStdvDist));
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public DistributionI<T> filterDist(DistributionI<T> dist) {
        Map<AttributeI, T> data = new HashMap<>();
        for (Entry<AttributeI, T> e : dist.entrySet()) {
            if (!blacklist.contains(e.getKey())) {
                data.put(e.getKey(), e.getValue());
            }
        }
        DistributionI<T> ret = null;
        if (dist.getEntryType().equals(Long.class)) {
            Map<AttributeI, Long> tmp = (Map<AttributeI, Long>) data;
            ret = (DistributionI<T>) new Distribution(dist.getMaster(), dist.getAttributeType(), tmp);
        } else if (dist.getEntryType().equals(Double.class)) {
              Map<AttributeI, Double> tmp = (Map<AttributeI, Double>) data;
            ret =  (DistributionI<T>) new NormalizedDistribution(dist.getMaster(), dist.getAttributeType(), tmp, dist.getTotalClassifiedElements());
        }
        return ret;
    }
}
