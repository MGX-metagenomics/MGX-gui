/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.testutils.TestMaster;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class IntervalTest {

    public IntervalTest() {
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
    public void testEquals() throws MGXException {
        System.out.println("equals");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);

        Interval i1 = new Interval(cache.getSegmentSize(), 0);
        Interval i2 = new Interval(cache.getSegmentSize(), 0);
        Interval i3 = new Interval(cache.getSegmentSize(), 1);
        assertEquals(i1, i2);
        assertNotEquals(i1, i3);
    }

    @Test
    public void testHashCode() throws MGXException {
        System.out.println("hashCode");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);

        Interval i1 = new Interval(cache.getSegmentSize(), 0);
        Interval i2 = new Interval(cache.getSegmentSize(), 0);
        Interval i3 = new Interval(cache.getSegmentSize(), 1);
        assertEquals(i1.hashCode(), i2.hashCode());
        assertNotEquals(i1.hashCode(), i3.hashCode());
    }

}
