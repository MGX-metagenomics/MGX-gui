/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sj
 */
public class CLRTransform implements VisFilterI<DistributionI<Double>, DistributionI<Double>> {

    @Override
    public List<Pair<GroupI, DistributionI<Double>>> filter(List<Pair<GroupI, DistributionI<Double>>> dists) {
        List<Pair<GroupI, DistributionI<Double>>> ret = new ArrayList<>(dists.size());
        for (Pair<GroupI, DistributionI<Double>> pair : dists) {
            ret.add(new Pair<>(pair.getFirst(), transformCLR(pair.getSecond())));
        }
        return ret;
    }

    public DistributionI<Double> transformCLR(DistributionI<Double> dist) {

        AttributeI[] attrs = new AttributeI[dist.size()];
        double[] counts = new double[dist.size()];

        int i = 0;
        for (Map.Entry<AttributeI, Double> e : dist.entrySet()) {
            attrs[i] = e.getKey();
            counts[i] = e.getValue();
        }

        MGXMasterI master = dist.getMaster();
        double[] clr;
        try {
            clr = master.Statistics().toCLR(counts);
        } catch (MGXException ex) {
            Logger.getLogger(CLRTransform.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        Map<AttributeI, Double> map = new HashMap<>(dist.size());
        for (i = 0; i < dist.size(); i++) {
            map.put(attrs[i], clr[i]);
        }

        return new NormalizedDistribution(dist.getMaster(), dist.getAttributeType(), map, dist.keySet(), dist.getTotalClassifiedElements());
    }
}
