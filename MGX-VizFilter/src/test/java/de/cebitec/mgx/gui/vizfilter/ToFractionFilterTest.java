/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.vizfilter;

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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class ToFractionFilterTest {

    @Test
    public void testFilter() {
        System.out.println("filter");
        List<Pair<VisualizationGroupI, DistributionI<Long>>> dists = new ArrayList<>();
        ToFractionFilter filter = new ToFractionFilter();
        List<Pair<VisualizationGroupI, DistributionI<Double>>> result = filter.filter(dists);
        assertNotNull(result);
        assertEquals(dists.size(), result.size());
        assertNotSame(dists, result);
    }

    @Test
    public void testTotal() {
        System.out.println("testTotal");

        Map<AttributeI, Long> map = new HashMap<>();
        AttributeI a1 = new Attribute(null);
        a1.setValue("FOO");
        map.put(a1, Long.valueOf(5));
        AttributeI a2 = new Attribute(null);
        a2.setValue("BAR");
        map.put(a2, Long.valueOf(5));
        DistributionI<Long> dist = new Distribution(null, map, 10);
        assertEquals(2, dist.size());
        assertEquals(10, dist.getTotalClassifiedElements());

        DistributionI<Double> filtered = new ToFractionFilter().filterDist(dist);
        assertEquals(10, filtered.getTotalClassifiedElements());
    }

}
