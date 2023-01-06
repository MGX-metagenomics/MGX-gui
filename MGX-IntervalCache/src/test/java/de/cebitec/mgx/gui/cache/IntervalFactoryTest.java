/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache;

import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
public class IntervalFactoryTest {

    @Test
    public void testSegments() {
        System.out.println("testSegments");
        Iterator<Interval> iter = IntervalFactory.createSegments(1049908, 1050000, 50000);
        assertNotNull(iter);
        Interval i = null;

        assertTrue(iter.hasNext());
        i = iter.next();
        assertEquals(1000000, i.getFrom());
        assertEquals(1049999, i.getTo());

        assertTrue(iter.hasNext());
        i = iter.next();
        assertEquals(1050000, i.getFrom());
        assertEquals(1099999, i.getTo());
        
        assertFalse(iter.hasNext());
    }

    @Test
    public void testSlidingWindow() {
        System.out.println("slidingWindow");
        int from = 0;
        int to = 9;
        int size = 3;
        Iterator<Interval> iter = IntervalFactory.slidingWindow(from, to, size, 1);
        assertNotNull(iter);
        while (iter.hasNext()) {
            Interval i = iter.next();
            assertNotNull(i);
            System.err.println("  " + i.getFrom() + "-" + i.getTo());
            assertEquals(size, i.length());
            assertTrue(from <= i.getFrom());
            assertTrue(to >= i.getTo());
        }

    }

}
