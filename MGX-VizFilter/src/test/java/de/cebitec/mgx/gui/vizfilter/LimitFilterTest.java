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
import de.cebitec.mgx.gui.vizfilter.LimitFilter.LIMITS;
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
public class LimitFilterTest {

    @Test
    public void testFilter() {
        System.out.println("filter");

        Map<AttributeI, Long> map = new HashMap<>();
        AttributeI a1 = new Attribute();
        a1.setValue("FOO");
        map.put(a1, Long.valueOf(5));
        AttributeI a2 = new Attribute();
        a2.setValue("BAR");
        map.put(a2, Long.valueOf(5));
        DistributionI<Long> dist = new Distribution(null, null, map);
        List<Pair<GroupI, DistributionI<Long>>> list = new ArrayList<>();
        VisualizationGroupI vg = null;
        list.add(new Pair<>(vg, dist));

        LimitFilter<Long> filter = new LimitFilter<>(LIMITS.ALL);
        assertNotNull(filter);

        List<Pair<GroupI, DistributionI<Long>>> filtered = filter.filter(list);
        assertEquals(1, filtered.size());
        assertNotSame(filtered, list);
    }

    @Test
    public void testFilter2() {
        System.out.println("filter2");

        Map<AttributeI, Long> map = new HashMap<>();
        for (long i = 1; i <= 11; i++) {
            AttributeI a1 = new Attribute();
            a1.setValue(UUID.randomUUID().toString());
            map.put(a1, 10 * i);
        }

        DistributionI<Long> dist = new Distribution(null, null, map);
        List<Pair<GroupI, DistributionI<Long>>> list = new ArrayList<>();
        VisualizationGroupI vg = null;
        list.add(new Pair<>(vg, dist));

        assertEquals(11, dist.size());
        assertEquals(660, dist.getTotalClassifiedElements());

        LimitFilter<Long> filter = new LimitFilter<>(LIMITS.TOP10);

        List<Pair<GroupI, DistributionI<Long>>> filtered = filter.filter(list);

        DistributionI<Long> d = filtered.get(0).getSecond();
        assertNotNull(d);

        assertEquals(10, d.size());
        // even after filtering, the total number of elements has to be the same as before
        assertEquals(660, d.getTotalClassifiedElements());
    }

}
