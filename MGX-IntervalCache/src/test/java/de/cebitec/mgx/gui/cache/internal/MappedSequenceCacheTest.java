/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache.internal;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import de.cebitec.mgx.gui.cache.CacheFactory;
import de.cebitec.mgx.gui.cache.CoverageInfoCache;
import de.cebitec.mgx.testutils.TestMaster;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class MappedSequenceCacheTest {

    public MappedSequenceCacheTest() {
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
    public void testCoveragePriv() throws MGXException {
        System.err.println("testCoveragePriv");
        MGXMasterI master = TestMaster.getPrivate("FIXME");
        Assume.assumeNotNull(master);
        Iterator<MappingI> iter = master.Mapping().fetchall();
        MappingI mapping = null;
        while (iter.hasNext()) {
            mapping = iter.next();
        }
        assertNotNull(mapping);
        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
        UUID uuid = master.Mapping().openMapping(1);
        CoverageInfoCache<Set<MappedSequenceI>> cache = CacheFactory.createMappedSequenceCache(master, ref, uuid);
        //
        //
        int[] foo = new int[]{0};
        cache.getCoverage(567083, 567083, foo);

        assertEquals(8901, foo[0]);
        master.Mapping().closeMapping(uuid);
    }

    @Test
    public void testMappingsPriv() throws MGXException {
        System.err.println("testMappingsPriv");
        MGXMasterI master = TestMaster.getPrivate("FIXME");
        Assume.assumeNotNull(master);
        Iterator<MappingI> iter = master.Mapping().fetchall();
        MappingI mapping = null;
        while (iter.hasNext()) {
            mapping = iter.next();
        }
        assertNotNull(mapping);
        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
        UUID uuid = master.Mapping().openMapping(1);
        CoverageInfoCache<Set<MappedSequenceI>> cache = CacheFactory.createMappedSequenceCache(master, ref, uuid);
        //
        //
        Set<MappedSequenceI> get = cache.get(567083, 567083);
        assertEquals(8901, get.size());
        master.Mapping().closeMapping(uuid);
    }

    @Test
    public void testCoverageMaxPriv() throws MGXException {
        System.err.println("testCoverageMaxPriv");
        MGXMasterI master = TestMaster.getPrivate("FIXME");
        Assume.assumeNotNull(master);
        Iterator<MappingI> iter = master.Mapping().fetchall();
        MappingI mapping = null;
        while (iter.hasNext()) {
            mapping = iter.next();
        }
        assertNotNull(mapping);
        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
        UUID uuid = master.Mapping().openMapping(1);
        CoverageInfoCache<Set<MappedSequenceI>> cache = CacheFactory.createMappedSequenceCache(master, ref, uuid);
        //
        //
        int cov = cache.getMaxCoverage(567083, 567083);

        assertEquals(8901, cov);
        master.Mapping().closeMapping(uuid);
    }

    @Test
    public void testOverlaps() {
        System.err.println("testOverlaps");
        // 6391-6796 outside of 0-49999
        MappedSequenceI ms = new MockMappedSequence(1, 6391, 6796, 1);
        boolean overlaps = MappedSequenceCache.overlaps(ms, 0, 49999);
        assertTrue(overlaps);
    }

    @Test
    public void testMappedSeqs() throws Exception {
        System.out.println("testMappedSeqs");
        MGXMasterI master = TestMaster.getRO();
        MGXReferenceI ref = master.Reference().fetch(8);
        UUID uuid = master.Mapping().openMapping(30);
        assertNotNull(uuid);

        CoverageInfoCache<Set<MappedSequenceI>> cache = CacheFactory.createMappedSequenceCache(master, ref, uuid);
        assertNotNull(cache);

        Set<MappedSequenceI> set = cache.get(566470, 566480);

        master.Mapping().closeMapping(uuid);
        //assertEquals(3, set.size());

        for (long l : new long[]{53748, 3436, 26467}) {
            boolean present = false;
            for (MappedSequenceI ms : set) {
                if (ms.getSeqId() == l) {
                    present = true;
                }
            }
            assertTrue("expected seqid " + l + " not in result", present);
        }
    }
}
