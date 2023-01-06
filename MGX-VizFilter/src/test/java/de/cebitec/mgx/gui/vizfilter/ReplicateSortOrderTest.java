package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pblumenk
 */
public class ReplicateSortOrderTest {

    @Test
    public void testFilter() {
        System.out.println("filter");
        AttributeTypeI at = new AttributeType(null, 42, "TestAttrType", AttributeTypeI.VALUE_DISCRETE, AttributeTypeI.STRUCTURE_BASIC);
        ReplicateSortOrder filter = new ReplicateSortOrder(at, ReplicateSortOrder.DESCENDING);

        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> dists = new ArrayList<>();
        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> result = filter.filter(dists);
        assertNotNull(result);
        assertEquals(dists.size(), result.size());
        // all filters have to return a new list and MUST NOT modify the input
        assertNotSame(dists, result);
    }

    @Test
    public void testSortOrderRegression() {
        System.out.println("testSortOrderRegression");

        // two distributions which share only FOO
        Map<AttributeI, Double> mean1 = new HashMap<>();
        AttributeI a1 = new Attribute();
        a1.setValue("FOO");
        mean1.put(a1, 3.0);
        AttributeI a2 = new Attribute();
        a2.setValue("BAR");
        mean1.put(a2, 3.5);
        DistributionI<Double> meanDist1 = new NormalizedDistribution(null, null, mean1, 2);
        Map<AttributeI, Double> stdv1 = new HashMap<>();
        stdv1.put(a1, 1.0);
        stdv1.put(a2, 0.5);
        DistributionI<Double> stdvDist1 = new NormalizedDistribution(null, null, stdv1, 2);
        
        Map<AttributeI, Double> mean2 = new HashMap<>();
        AttributeI a3 = new Attribute();
        a3.setValue("FOO");
        mean2.put(a3, 5.0);
        AttributeI a4 = new Attribute();
        a4.setValue("BAZ");
        mean2.put(a4, 2.0);
        DistributionI<Double> meanDist2 = new NormalizedDistribution(null, null, mean2, 2);
        Map<AttributeI, Double> stdv2 = new HashMap<>();
        stdv2.put(a1, 2.0);
        stdv2.put(a3, 1.5);
        DistributionI<Double> stdvDist2 = new NormalizedDistribution(null, null, stdv2, 2);

        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> in = new ArrayList<>();
        ReplicateGroupI dummyRG = null;
        in.add(new Triple<>(dummyRG, meanDist1, stdvDist1));
        in.add(new Triple<>(dummyRG, meanDist2, stdvDist2));

        AttributeTypeI at = new AttributeType(null, 42, "TestAttrType", AttributeTypeI.VALUE_DISCRETE, AttributeTypeI.STRUCTURE_BASIC);
        ReplicateSortOrder so = new ReplicateSortOrder(at, ReplicateSortOrder.DESCENDING);

        List<Triple<ReplicateGroupI, DistributionI<Double>, DistributionI<Double>>> result = so.filter(in);
        assertNotNull(result);
        assertEquals(2, result.size());
        
        meanDist1 = result.get(0).getSecond();
        stdvDist1 = result.get(0).getThird();
        assertEquals(2, meanDist1.getTotalClassifiedElements());
        assertEquals(2, stdvDist1.getTotalClassifiedElements());
        
        // merged distribution is
        // FOO 3+5=8
        // BAR 3.5 
        // BAZ 2
        
        // verify order of first dist - "FOO" has to appear first since
        // sorting order is determined by the summarized data
        Set<AttributeI> meanKeySet = meanDist1.keySet();
        Set<AttributeI> stdvKeySet = stdvDist1.keySet();
        assertEquals(2, meanKeySet.size());
        assertEquals(2, stdvKeySet.size());
        AttributeI[] meanAttrOrdered = meanKeySet.toArray(new AttributeI[]{});
        AttributeI[] stdvAttrOrdered = stdvKeySet.toArray(new AttributeI[]{});
        assertEquals("FOO", stdvAttrOrdered[0].getValue());
        assertEquals("FOO", stdvAttrOrdered[0].getValue());
    }

}
