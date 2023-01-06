/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import de.cebitec.mgx.gui.vizfilter.ReplicateLimitFilter.LIMITS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
public class ReplicateLimitFilterTest {

    @Test
    public void testFilter() {
        System.out.println("filter");

        Map<AttributeI, Double> mean = new HashMap<>();
        AttributeI a1 = new Attribute();
        a1.setValue("FOO");
        mean.put(a1, 3.0);
        AttributeI a2 = new Attribute();
        a2.setValue("BAR");
        mean.put(a2, 3.5);
        DistributionI<Double> meanDist = new NormalizedDistribution(null, null, mean, 2);
        Map<AttributeI, Double> stdv = new HashMap<>();
        stdv.put(a1, 1.0);
        stdv.put(a2, 0.5);
        DistributionI<Double> stdvDist = new NormalizedDistribution(null, null, stdv, 2);
        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> list = new ArrayList<>();
        ReplicateGroupI rg = null;
        list.add(new Triple<>(rg, meanDist, stdvDist));

        ReplicateLimitFilter filter = new ReplicateLimitFilter(LIMITS.ALL);
        assertNotNull(filter);

        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> filtered = filter.filter(list);
        assertEquals(1, filtered.size());
        assertEquals(2, filtered.get(0).getSecond().size());
        assertNotSame(filtered, list);
    }

    @Test
    public void testFilter2() {
        System.out.println("filter2");

        Map<AttributeI, Double> mean = new HashMap<>();
        Map<AttributeI, Double> stdv = new HashMap<>();
        for (long i = 1; i <= 11; i++) {
            AttributeI a1 = new Attribute();
            a1.setValue(UUID.randomUUID().toString());
            mean.put(a1, 10.0 * i);
            stdv.put(a1, 0.1 * i);
        }

        DistributionI<Double> meanDist = new NormalizedDistribution(null, null, mean, 50);
        DistributionI<Double> stdvDist = new NormalizedDistribution(null, null, stdv, 50);
        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> list = new ArrayList<>();
        ReplicateGroupI rg = null;
        list.add(new Triple<>(rg, meanDist, stdvDist));

        assertEquals(11, meanDist.size());
        assertEquals(11, stdvDist.size());
        assertEquals(50, meanDist.getTotalClassifiedElements());
        assertEquals(50, stdvDist.getTotalClassifiedElements());

        ReplicateLimitFilter filter = new ReplicateLimitFilter(LIMITS.TOP10);

        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> filtered = filter.filter(list);

        DistributionI<Double> m = filtered.get(0).getSecond();
        DistributionI<Double> s = filtered.get(0).getThird();
        assertNotNull(m);
        assertNotNull(s);

        assertEquals(10, m.size());
        assertEquals(10, s.size());
        // even after filtering, the total number of elements has to be the same as before
        assertEquals(50, m.getTotalClassifiedElements());
        assertEquals(50, s.getTotalClassifiedElements());
    }

}
