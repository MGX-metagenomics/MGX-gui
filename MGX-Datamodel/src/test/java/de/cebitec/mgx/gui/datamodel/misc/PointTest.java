/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.datamodel.misc;

import de.cebitec.mgx.api.misc.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class PointTest {

    public PointTest() {
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
    public void testCompareTo() {
        System.out.println("comparePoints");
        // points should autosort based on length, ascending
        Point p1 = new Point(1, 1);
        Point p2 = new Point(2, 2);
        List<Point> l = new ArrayList<>(2);
        l.add(p1);
        l.add(p2);
        Collections.sort(l);
        assertEquals(p1, l.get(0));
        assertEquals(p2, l.get(1));
    }

}
