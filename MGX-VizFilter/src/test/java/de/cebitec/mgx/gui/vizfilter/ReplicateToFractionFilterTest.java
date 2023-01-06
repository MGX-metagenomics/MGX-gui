package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pblumenk
 */
public class ReplicateToFractionFilterTest {

    @Test
    public void testFilter() {
        System.out.println("filter");
        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> dists = new ArrayList<>();
        ReplicateToFractionFilter filter = new ReplicateToFractionFilter();
        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> result = filter.filter(dists);
        assertNotNull(result);
        assertEquals(dists.size(), result.size());
        assertNotSame(dists, result);
    }

    @Test
    public void testTotal() {
        System.out.println("testTotal");

        Map<AttributeI, Double> mean = new HashMap<>();
        AttributeI a1 = new Attribute();
        a1.setValue("FOO");
        mean.put(a1, 2.5);
        AttributeI a2 = new Attribute();
        a2.setValue("BAR");
        mean.put(a2, 2.5);
        DistributionI<Double> meanDist = new NormalizedDistribution(null, null, mean, 10);
        assertEquals(2, meanDist.size());
        assertEquals(10, meanDist.getTotalClassifiedElements());
        Map<AttributeI, Double> stdv = new HashMap<>();
        stdv.put(a1, 1.0);
        stdv.put(a2, 0.5);
        DistributionI<Double> stdvDist = new NormalizedDistribution(null, null, stdv, 10);
        assertEquals(2, stdvDist.size());
        assertEquals(10, stdvDist.getTotalClassifiedElements());
        
        Pair<DistributionI<Double>, DistributionI<Double>> filtered = new ReplicateToFractionFilter().filterDist(meanDist, stdvDist);
        meanDist = filtered.getFirst();
        stdvDist = filtered.getSecond();
        assertEquals(10, meanDist.getTotalClassifiedElements());
        assertEquals(10, stdvDist.getTotalClassifiedElements());
        assertEquals(0.5, meanDist.get(a1), 0.01);
        assertEquals(0.2, stdvDist.get(a1), 0.01);
        assertEquals(0.5, meanDist.get(a2), 0.01);
        assertEquals(0.1, stdvDist.get(a2), 0.01);
    }

}
