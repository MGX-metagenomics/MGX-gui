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
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
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
 * @author sjaenick
 */
public class ToFractionFilterTest {

    @Test
    public void testFilter() {
        System.out.println("filter");
        List<Pair<GroupI, DistributionI<Long>>> dists = new ArrayList<>();
        ToFractionFilter filter = new ToFractionFilter();
        List<Pair<GroupI, DistributionI<Double>>> result = filter.filter(dists);
        assertNotNull(result);
        assertEquals(dists.size(), result.size());
        assertNotSame(dists, result);
    }

    @Test
    public void testTotal() {
        System.out.println("testTotal");

        Map<AttributeI, Long> map = new HashMap<>();
        AttributeI a1 = new Attribute();
        a1.setValue("FOO");
        map.put(a1, Long.valueOf(5));
        AttributeI a2 = new Attribute();
        a2.setValue("BAR");
        map.put(a2, Long.valueOf(5));
        DistributionI<Long> dist = new Distribution(null, null, map, 10);
        assertEquals(2, dist.size());
        assertEquals(10, dist.getTotalClassifiedElements());

        DistributionI<Double> filtered = new ToFractionFilter().filterDist(dist);
        assertEquals(10, filtered.getTotalClassifiedElements());
    }

}
