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
import java.util.HashSet;
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
public class ExcludeFilterTest {

    @Test
    public void testFilter() {
        System.out.println("filterDist");

        Map<AttributeI, Long> map = new HashMap<>();
        AttributeI a1 = new Attribute();
        a1.setValue("FOO");
        map.put(a1, Long.valueOf(5));
        AttributeI a2 = new Attribute();
        a2.setValue("BAR");
        map.put(a2, Long.valueOf(5));
        DistributionI<Long> dist = new Distribution(null, null, map);
        assertEquals(2, dist.size());

        Set<AttributeI> blacklist = new HashSet<>();
        blacklist.add(a2);

        ExcludeFilter<Long> ef = new ExcludeFilter<>(blacklist);
        assertNotNull(ef);

        List<Pair<GroupI, DistributionI<Long>>> xx = new ArrayList<>();
        VisualizationGroupI vg = null;
        xx.add(new Pair<>(vg, dist));

        List<Pair<GroupI, DistributionI<Double>>> yy = new LongToDouble().filter(xx);
        
        ExcludeFilter<Double> ef2 = new ExcludeFilter<>(blacklist);
        DistributionI<Double> filtered = ef2.filterDist(yy.get(0).getSecond());
        assertEquals(1, filtered.size());

        assertNotSame(filtered, dist);
    }

}
