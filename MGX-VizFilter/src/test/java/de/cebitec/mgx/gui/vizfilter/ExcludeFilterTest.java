/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class ExcludeFilterTest {

    @Test
    public void testFilter() {
        System.out.println("filterDist");

        Map<AttributeI, Long> map = new HashMap<>();
        AttributeI a1 = new Attribute(null);
        a1.setValue("FOO");
        map.put(a1, Long.valueOf(5));
        AttributeI a2 = new Attribute(null);
        a2.setValue("BAR");
        map.put(a2, Long.valueOf(5));
        DistributionI<Long> dist = new Distribution(null, map);
         assertEquals(2, dist.size());
        
        Set<AttributeI> blacklist = new HashSet<>();
        blacklist.add(a2);
        
        ExcludeFilter<Long> ef = new ExcludeFilter<>(blacklist);
        assertNotNull(ef);
        
        DistributionI<Long> filtered = ef.filterDist(dist);
        assertEquals(1, filtered.size());
        
        assertNotSame(filtered, dist);
    }

}