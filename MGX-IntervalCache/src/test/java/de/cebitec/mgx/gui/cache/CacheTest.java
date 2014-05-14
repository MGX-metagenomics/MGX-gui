package de.cebitec.mgx.gui.cache;

import de.cebitec.gpms.core.MembershipI;
import de.cebitec.gpms.rest.GPMSClientI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.gui.cache.internal.Interval;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.datamodel.Region;
import de.cebitec.mgx.restgpms.GPMS;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class CacheTest {

    public CacheTest() {
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
    public void testGetSequence() throws Exception {
        System.out.println("getSequence");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        String seq = cache.get(0, 9);
        assertEquals("ttgtgcacac", seq);

        seq = cache.get(97, 101);
        assertEquals("attcg", seq);
    }

    @Test
    public void testGetFullSequence() throws Exception {
        System.out.println("getFullSequence");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        String seq = cache.get(0, ref.getLength() - 1);
        assertEquals(seq.length(), ref.getLength());
    }

    @Test
    public void testGetRegions() throws Exception {
        System.out.println("getRegions");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(4);
        Cache<Set<Region>> cache = CacheFactory.createRegionCache(master, ref);
        assertNotNull(cache);
        Set<Region> data = cache.get(100, 2850);
        assertEquals(2, data.size());
    }

    @Test
    public void testGetRegions2() throws Exception {
        System.out.println("getRegions2");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(4);
        Cache<Set<Region>> cache = CacheFactory.createRegionCache(master, ref);
        assertNotNull(cache);
        Set<Region> data = cache.get(0, ref.getLength() - 1);
        assertEquals(8750, data.size());
    }

    @Test
    public void testGetOneInterval() {
        System.out.println("getOneInterval");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        Iterator<Interval<String>> iter = cache.getIntervals(5, 10);
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        Interval<String> interval = iter.next();
        assertNotNull(interval);
        assertFalse(iter.hasNext());
        assertEquals(0, interval.getFrom());
        assertEquals(cache.getSegmentSize() - 1, interval.getTo());
    }

    @Test
    public void testGetIntervals() {
        System.out.println("getIntervals");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        System.err.println("  generating " + 0 + " to " + (ref.getLength() - 1));
        Iterator<Interval<String>> iter = cache.getIntervals(0, ref.getLength() - 1);
        assertNotNull(iter);
        Interval<String> interval = null;
        int numSegments = 0;
        while (iter.hasNext()) {
            interval = iter.next();
            assertNotNull(interval);
            numSegments++;
            System.err.println("  " + interval.getFrom() + " - " + interval.getTo() + " with len " + ref.getLength());
            assertTrue(interval.getFrom() > -1);
            assertTrue(interval.getTo() > -1);
            assertTrue(interval.getFrom() < ref.getLength());
            assertTrue(interval.getFrom() <= interval.getTo());
            assertEquals(cache.getSegmentSize(), interval.length());
        }
        assertEquals(214, numSegments);
    }

    @Test
    public void testIntervalAlignment() {
        System.out.println("IntervalAlignment");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        Iterator<Interval<String>> iter = cache.getIntervals(5, 999999);
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        Interval<String> interval = iter.next();
        assertNotNull(interval);
        System.err.println("  " + interval.getFrom() + " - " + interval.getTo() + " with len " + ref.getLength());
        assertEquals(0, interval.getFrom());
        assertEquals(cache.getSegmentSize() - 1, interval.getTo());
        assertEquals(cache.getSegmentSize(), interval.length());
    }

    @Test
    public void testIntervalAlignment2() {
        System.out.println("IntervalAlignment2");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        int from = cache.getSegmentSize() + 5;
        int to = 2 * cache.getSegmentSize() + 5;
        System.err.println("  generating " + from + " to " + to);
        Iterator<Interval<String>> iter = cache.getIntervals(from, to);
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        int numSegments = 0;
        Interval<String> interval = null;
        while (iter.hasNext()) {
            if (interval == null) {
                interval = iter.next();
                System.err.println("  " + interval.getFrom() + " - " + interval.getTo() + " with len " + ref.getLength());
                // check first interval
                assertEquals(cache.getSegmentSize(), interval.getFrom());
                assertEquals(cache.getSegmentSize(), interval.getFrom());
            } else {
                interval = iter.next();
                System.err.println("  " + interval.getFrom() + " - " + interval.getTo() + " with len " + ref.getLength());
            }
            numSegments++;
            assertNotNull(interval);
            assertTrue(interval.getFrom() > -1);
            assertTrue(interval.getTo() > -1);
            assertTrue(interval.getFrom() < ref.getLength());
            assertTrue(interval.getTo() < ref.getLength());
            assertTrue(interval.getFrom() < interval.getTo());
        }
        assertEquals(149999, interval.getTo());
        assertEquals(2, numSegments);
    }

    @Test
    public void testCacheing() {
        System.out.println("Cacheing");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(4);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);

        Iterator<Interval<String>> iter = cache.getIntervals(0, 99);
        assertTrue(iter.hasNext());
        Interval<String> interval = iter.next();
        assertFalse(cache.contains(interval));

        String seq = null;
        seq = cache.get(0, 99);
        assertNotNull(seq);

        // same ref
        assertTrue(cache.contains(interval));

        iter = cache.getIntervals(0, 99);
        assertTrue(iter.hasNext());
        interval = iter.next();
        // different ref, but equal
        assertTrue(cache.contains(interval));
    }

    public static MGXMaster get() {
        MGXMaster master = null;

        String serverURI = "https://mgx.cebitec.uni-bielefeld.de/MGX-maven-web/webresources/";

        String config = System.getProperty("user.home") + "/.m2/mgx.junit";
        File f = new File(config);
        if (f.exists() && f.canRead()) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(f));
                serverURI = p.getProperty("testserver");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        GPMSClientI gpms = new GPMS("MyServer", serverURI);
        if (!gpms.login("mgx_unittestRO", "gut-isM5iNt")) {
            fail();
        }
        for (MembershipI m : gpms.getMemberships()) {
            if ("MGX".equals(m.getProject().getProjectClass().getName()) && ("MGX_Unittest".equals(m.getProject().getName()))) {
                MGXDTOMaster dtomaster = new MGXDTOMaster(gpms, m);
                master = new MGXMaster(dtomaster);
                break;
            }
        }

        assert master != null;
        return master;
    }

}
