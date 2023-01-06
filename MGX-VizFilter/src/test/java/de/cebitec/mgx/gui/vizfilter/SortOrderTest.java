/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.GroupI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
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
 * @author sjaenick
 */
public class SortOrderTest {

    @Test
    public void testFilter() {
        System.out.println("filter");
        SortOrder<Long> filter = new SortOrder<>(SortOrder.DESCENDING);

        List<Pair<GroupI, DistributionI<Long>>> dists = new ArrayList<>();
        List<Pair<GroupI, DistributionI<Long>>> result = filter.filter(dists);
        assertNotNull(result);
        assertEquals(dists.size(), result.size());
        // all filters have to return a new list and MUST NOT modify the input
        assertNotSame(dists, result);
    }

    @Test
    public void testSortOrderRegression() {
        System.out.println("testSortOrderRegression");

        // two distributions which share only FOO
        Map<AttributeI, Long> map = new HashMap<>();
        AttributeI a1 = new Attribute();
        a1.setValue("FOO");
        map.put(a1, Long.valueOf(2));
        AttributeI a2 = new Attribute();
        a2.setValue("BAR");
        map.put(a2, Long.valueOf(4));
        DistributionI<Long> dist1 = new Distribution(null, null, map);

        Map<AttributeI, Long> map2 = new HashMap<>();
        AttributeI a3 = new Attribute();
        a3.setValue("FOO");
        map2.put(a3, Long.valueOf(3));
        AttributeI a4 = new Attribute();
        a4.setValue("BAZ");
        map2.put(a4, Long.valueOf(2));
        DistributionI<Long> dist2 = new Distribution(null, null, map2);

        List<Pair<GroupI, DistributionI<Long>>> in = new ArrayList<>();
        VisualizationGroupI dummyVG = null;
        in.add(new Pair<>(dummyVG, dist1));
        in.add(new Pair<>(dummyVG, dist2));

        SortOrder<Long> so = new SortOrder<>(SortOrder.DESCENDING);

        List<Pair<GroupI, DistributionI<Long>>> result = so.filter(in);
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // merged distribution is
        // FOO 2+3=5
        // BAR 4 
        // BAZ 2
        
        DistributionI<Long> firstDist = result.get(0).getSecond();
        assertEquals(6, firstDist.getTotalClassifiedElements());
        
        // verify order of first dist - "FOO" has to appear first since
        // sorting order is determined by the summarized data
        Set<AttributeI> keySet = firstDist.keySet();
        assertEquals(2, keySet.size());
        AttributeI[] attrOrdered = keySet.toArray(new AttributeI[]{});
        assertEquals("FOO", attrOrdered[0].getValue());
    }

}
