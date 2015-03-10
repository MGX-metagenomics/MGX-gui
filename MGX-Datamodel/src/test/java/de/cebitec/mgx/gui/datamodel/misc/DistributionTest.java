/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.gui.datamodel.Attribute;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sjaenick
 */
public class DistributionTest {

    public DistributionTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testEquals() {
        System.out.println("equalsDistribution");

        Map<AttributeI, Long> d1 = new HashMap<>();
        Attribute a1 = new Attribute(null);
        a1.setValue("FOO");
        d1.put(a1, Long.valueOf(5));
        Distribution dist1 = new Distribution(null, d1);

        Map<AttributeI, Long> d2 = new HashMap<>();
        Attribute a2 = new Attribute(null);
        a2.setValue("FOO");
        d2.put(a2, Long.valueOf(5));
        Distribution dist2 = new Distribution(null, d2);

        Map<AttributeI, Long> d3 = new HashMap<>();
        Attribute a3 = new Attribute(null);
        a3.setValue("BAR");
        d3.put(a3, Long.valueOf(5));
        Distribution dist3 = new Distribution(null, d3);

        Map<AttributeI, Long> d4 = new HashMap<>();
        Attribute a4 = new Attribute(null);
        a4.setValue("BAR");
        d4.put(a4, Long.valueOf(6));
        Distribution dist4 = new Distribution(null, d4);

        assertEquals(dist1, dist2);
        assertNotEquals(dist2, dist3);
        assertNotEquals(dist3, dist4);
    }

}
