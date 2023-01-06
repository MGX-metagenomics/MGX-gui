/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.misc.NormalizedDistribution;
import java.util.HashMap;
import java.util.HashSet;
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
public class ReplicateExcludeFilterTest {

    @Test
    public void testFilter() {
        System.out.println("filterDist");

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
         assertEquals(2, meanDist.size());
         assertEquals(2, stdvDist.size());
        
        Set<AttributeI> blacklist = new HashSet<>();
        blacklist.add(a2);
        
        ReplicateExcludeFilter<Double> ef = new ReplicateExcludeFilter<>(blacklist);
        assertNotNull(ef);
        
        DistributionI<Double> filteredMean = ef.filterDist(meanDist);
        DistributionI<Double> filteredStdv = ef.filterDist(stdvDist);
        assertEquals(1, filteredMean.size());
        assertEquals(1, filteredStdv.size());
        
        assertNotSame(filteredMean, meanDist);
        assertNotSame(filteredStdv, stdvDist);
    }

}
