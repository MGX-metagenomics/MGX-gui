package de.cebitec.mgx.gui.cache;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import de.cebitec.mgx.testutils.TestMaster;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author sjaenick
 */
public class CacheTest {

    public CacheTest() {
    }

    @Test
    public void testGetSequenceRegression() throws Exception {
        System.out.println("testGetSequenceRegression");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        String seq = cache.get(1049908, 1050000);
        assertNotNull(seq);
    }

    @Test
    public void testGetSequence() throws Exception {
        System.out.println("getSequence");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        String seq = cache.get(0, 9);
        assertEquals("CCTGGTTTGT", seq);

        seq = cache.get(97, 101);
        assertEquals("TTCTT", seq);
    }

    @Test
    public void testGetMappedSeqs() throws Exception {
        System.out.println("getMappedSeqs");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        assertNotNull(ref);
        MappingI m = master.Mapping().fetch(1);
        assertNotNull(m);
        assertEquals(65, m.getReferenceID());
        UUID sessionUUID = master.Mapping().openMapping(m.getId());
        CoverageInfoCache<Set<MappedSequenceI>> cache = CacheFactory.createMappedSequenceCache(master, ref, sessionUUID);
        assertNotNull(cache);

        Set<MappedSequenceI> ret = cache.get(0, 1000);
        assertNotNull(ret);
        assertEquals(0, ret.size());

        master.Mapping().closeMapping(sessionUUID);
    }

    @Test
    public void testGetFullSequence() throws Exception {
        System.out.println("getFullSequence");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        String seq = cache.get(0, ref.getLength() - 1);
        assertEquals(seq.length(), ref.getLength());
    }

    @Test
    public void testGetRegions() throws Exception {
        System.out.println("getRegions");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        Cache<Set<ReferenceRegionI>> cache = CacheFactory.createRegionCache(master, ref);
        assertNotNull(cache);
        Set<ReferenceRegionI> data = cache.get(100, 2850);
        assertEquals(3, data.size());
    }

    @Test
    public void testGetRegions2() throws Exception {
        System.out.println("getRegions2");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        Cache<Set<ReferenceRegionI>> cache = CacheFactory.createRegionCache(master, ref);
        assertNotNull(cache);
        Set<ReferenceRegionI> data = cache.get(0, ref.getLength() - 1);
        assertEquals(2656, data.size());
    }

    @Test
    public void testGetOneInterval() throws MGXException {
        System.out.println("getOneInterval");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        Iterator<Interval> iter = cache.getIntervals(5, 10);
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        Interval interval = iter.next();
        assertNotNull(interval);
        assertFalse(iter.hasNext());
        assertEquals(0, interval.getFrom());
        assertEquals(cache.getSegmentSize() - 1, interval.getTo());
    }

    @Test
    public void testGetIntervals() throws MGXException {
        System.out.println("getIntervals");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        //System.err.println("  generating " + 0 + " to " + (ref.getLength() - 1));
        Iterator<Interval> iter = cache.getIntervals(0, ref.getLength() - 1);
        assertNotNull(iter);
        Interval interval;
        int numSegments = 0;
        while (iter.hasNext()) {
            interval = iter.next();
            assertNotNull(interval);
            numSegments++;
            //System.err.println("  " + interval.getFrom() + " - " + interval.getTo() + " with len " + ref.getLength());
            assertTrue(interval.getFrom() > -1);
            assertTrue(interval.getTo() > -1);
            assertTrue(interval.getFrom() < ref.getLength());
            assertTrue(interval.getFrom() <= interval.getTo());
            assertEquals(cache.getSegmentSize(), interval.length());
        }
        assertEquals(56, numSegments);
    }

    @Test
    public void testShortInterval() throws MGXException {
        System.out.println("getShortintervals");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        System.err.println("  generating " + 0 + " to " + 5);
        Iterator<Interval> iter = cache.getIntervals(0, 5);
        assertNotNull(iter);
        Interval interval;
        int numSegments = 0;
        while (iter.hasNext()) {
            interval = iter.next();
            assertNotNull(interval);
            assertEquals(0, interval.getFrom());
            assertEquals(49999, interval.getTo());
            numSegments++;
        }
        assertEquals(1, numSegments);
    }

    @Test
    public void testIntervalAlignment() throws MGXException {
        System.out.println("IntervalAlignment");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        Iterator<Interval> iter = cache.getIntervals(5, 999999);
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        Interval interval = iter.next();
        assertNotNull(interval);
        System.err.println("  " + interval.getFrom() + " - " + interval.getTo() + " with len " + ref.getLength());
        assertEquals(0, interval.getFrom());
        assertEquals(cache.getSegmentSize() - 1, interval.getTo());
        assertEquals(cache.getSegmentSize(), interval.length());
    }

    @Test
    public void testIntervalAlignment2() throws MGXException {
        System.out.println("IntervalAlignment2");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);
        int from = cache.getSegmentSize() + 5;
        int to = 2 * cache.getSegmentSize() + 5;
        System.err.println("  generating " + from + " to " + to);
        Iterator<Interval> iter = cache.getIntervals(from, to);
        assertNotNull(iter);
        assertTrue(iter.hasNext());
        int numSegments = 0;
        Interval interval = null;
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
        assertNotNull(interval);
        assertEquals(149999, interval.getTo());
        assertEquals(2, numSegments);
    }

    @Test
    public void testCacheing() throws MGXException {
        System.out.println("Cacheing");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        Cache<String> cache = CacheFactory.createSequenceCache(master, ref);
        assertNotNull(cache);

        Iterator<Interval> iter = cache.getIntervals(0, 99);
        assertTrue(iter.hasNext());
        Interval interval = iter.next();
        assertFalse(cache.contains(interval));

        String seq = cache.get(0, 99);
        assertNotNull(seq);

        // same ref
        assertTrue(cache.contains(interval));

        iter = cache.getIntervals(0, 99);
        assertTrue(iter.hasNext());
        interval = iter.next();
        // different ref, but equal
        assertTrue(cache.contains(interval));
    }

    @Test
    public void testIterator() throws Exception {
        System.out.println("getCovIterator");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        assertNotNull(ref);
        UUID uuid = master.Mapping().openMapping(1);

        CoverageInfoCache<Set<MappedSequenceI>> cache = CacheFactory.createMappedSequenceCache(master, ref, uuid);
        assertNotNull(cache);

        IntIterator iter = cache.getCoverageIterator(0, ref.getLength() - 1);
        int cnt = 0;
        while (iter.hasNext()) {
            iter.next();
            cnt++;
        }
        assertEquals(cnt, ref.getLength());
    }

    @Test
    public void testMaxCoverage() throws Exception {
        System.out.println("maxCoverage");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(65);
        assertNotNull(ref);
        UUID uuid = master.Mapping().openMapping(1);

        CoverageInfoCache<Set<MappedSequenceI>> cache = CacheFactory.createMappedSequenceCache(master, ref, uuid);
        assertNotNull(cache);

//        int maxCoverage = cache.getMaxCoverage(0, 5);
//        assertEquals(0, maxCoverage);
//
//        maxCoverage = cache.getMaxCoverage(150000, 150020);
//        assertEquals(0, maxCoverage);
//
//        maxCoverage = cache.getMaxCoverage(12780, 12785);
//        assertEquals(0, maxCoverage);
//        int[] foo = new int[15];
//        cache.getCoverage(566470, 566480, foo);
//        for (int i : foo) { System.err.println(foo[i]); }
        // expect seqs 53748, 3436, 26467

        int maxCoverage = cache.getMaxCoverage(566470, 566480);
        assertEquals(3, maxCoverage);

        maxCoverage = cache.getMaxCoverage(0, ref.getLength() - 1);
        assertEquals(3, maxCoverage);
//
//        IntIterator iter = cache.getCoverageIterator(0, ref.getLength() - 1);
//        int max = 0;
//        while (iter.hasNext()) {
//            int next = iter.next();
//            if (next > max) {
//                max = next;
//            }
//        }
//        assertEquals(maxCoverage, max);
    }

    private boolean containsSeqId(Set<MappedSequenceI> set, long id) {
        for (MappedSequenceI ms : set) {
            if (id == ms.getSeqId()) {
                return true;
            }
        }
        return false;
    }
}
