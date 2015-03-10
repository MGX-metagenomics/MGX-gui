/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.vizfilter;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import java.util.ArrayList;
import java.util.List;
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

}
