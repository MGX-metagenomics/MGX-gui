/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.cache.internal;

import de.cebitec.mgx.gui.cache.CacheFactory;
import static de.cebitec.mgx.gui.cache.CacheTest.get;
import de.cebitec.mgx.gui.cache.CoverageInfoCache;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MappedSequence;
import de.cebitec.mgx.gui.datamodel.Reference;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.UUID;
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
    public void testOverlaps() {
        System.err.println("testOverlaps");
        // 6391-6796 outside of 0-49999
        MappedSequence ms = new MappedSequence(1, 6391, 6796, 1);
        boolean overlaps = MappedSequenceCache.overlaps(ms, 0, 49999);
        assertTrue(overlaps);
    }
    
    @Test
    public void testMappedSeqs() throws Exception {
        System.out.println("testMappedSeqs");
        MGXMaster master = get();
        Reference ref = master.Reference().fetch(8);
        UUID uuid = master.Mapping().openMapping(30);
        assertNotNull(uuid);
        
        CoverageInfoCache<SortedSet<MappedSequence>> cache = CacheFactory.createMappedSequenceCache(master, ref, uuid);
        assertNotNull(cache);
        
        SortedSet<MappedSequence> set = cache.get(566470, 566480);
        
        master.Mapping().closeMapping(uuid);
        //assertEquals(3, set.size());
        
        for (long l : new long[]{53748, 3436, 26467}) {
            boolean present = false;
            for (MappedSequence ms : set) {
                if (ms.getSeqId() == l) {
                    present = true;
                }
            }
            assertTrue("expected seqid " + l + " not in result", present);
        }
    }
}
