/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.visualization.filter.VisFilterI;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class LongToDouble implements VisFilterI<DistributionI<Long>, DistributionI<Double>> {

    @Override
    public List<Pair<GroupI, DistributionI<Double>>> filter(List<Pair<GroupI, DistributionI<Long>>> in) {
        List<Pair<GroupI, DistributionI<Double>>> ret = new ArrayList<>();
        for (Pair<GroupI, DistributionI<Long>> p : in) {
            GroupI vg = p.getFirst();
            DistributionI<Long> d = p.getSecond();
            ret.add(new Pair<>(vg, filterDist(d)));
        }
        return ret;
    }

    public DistributionI<Double> filterDist(DistributionI<Long> d) {
        Map<AttributeI, Double> tmp = new HashMap<>();
        for (Map.Entry<AttributeI, Long> e : d.entrySet()) {
            tmp.put(e.getKey(), e.getValue().doubleValue());
        }
        DistributionI<Double> converted = new NormalizedDistribution(d.getMaster(), d.getAttributeType(), tmp, d.getTotalClassifiedElements());
        return converted;
    }

}
