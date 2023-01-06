/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.assembly;

import de.cebitec.mgx.common.RegionType;
import de.cebitec.mgx.gui.datamodel.ReferenceRegion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sj
 */
public class ReferenceRegionTest {

    @Test
    public void testGetFrame() {
        System.out.println("getFrame");
        ReferenceRegion g = new ReferenceRegion(null, -1, -1, 1, 42, RegionType.CDS, "noname");
        assertEquals(2, g.getFrame());
    }

    @Test
    public void testGetLength() {
        System.out.println("getLength");
        ReferenceRegion g = new ReferenceRegion(null, -1, -1, 1, 42, RegionType.CDS, "noname");
        assertEquals(42, g.getLength());
    }

    @Test
    public void testGetFrameReverse() {
        System.out.println("testGetFrameReverse");
        ReferenceRegion g = new ReferenceRegion(null, -1, -1, 230, 0, RegionType.CDS, "noname");
        assertEquals(-3, g.getFrame());
    }

    @Test
    public void testGetLengthReverse() {
        System.out.println("testGetLengthReverse");
        ReferenceRegion g = new ReferenceRegion(null, -1, -1, 230, 0, RegionType.CDS, "noname");
        assertEquals(231, g.getLength());
    }
}
