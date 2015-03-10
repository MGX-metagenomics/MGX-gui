package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
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
public class ExcludeFilter<T extends Number> implements VisFilterI<DistributionI<T>, DistributionI<T>> {

    private final Set<AttributeI> blacklist;

    public ExcludeFilter(Set<AttributeI> blacklist) {
        if (blacklist == null) {
           throw new IllegalArgumentException();
        }
        this.blacklist = Collections.unmodifiableSet(blacklist);
    }

    @Override
    public List<Pair<VisualizationGroupI, DistributionI<T>>> filter(List<Pair<VisualizationGroupI, DistributionI<T>>> in) {
        List<Pair<VisualizationGroupI, DistributionI<T>>> ret = new ArrayList<>(in.size());
        for (Pair<VisualizationGroupI, DistributionI<T>> p : in) {
            DistributionI<T> filteredDist = filterDist(p.getSecond());
            ret.add(new Pair<>(p.getFirst(), filteredDist));
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
            ret = (DistributionI<T>) new Distribution(dist.getMaster(), tmp);
        } else if (dist.getEntryType().equals(Double.class)) {
              Map<AttributeI, Double> tmp = (Map<AttributeI, Double>) data;
            ret =  (DistributionI<T>) new NormalizedDistribution(dist.getMaster(), tmp);
        }
        return ret;
    }
}
