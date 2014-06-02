/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.MappedSequenceI;
import de.cebitec.mgx.api.model.MappingI;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sj
 */
public class MappingCtxTest {

    public MappingCtxTest() {
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

    /**
     * Test of getSequence method, of class MappingCtx.
     */
    @Test
    public void testGetSequence() {
        System.out.println("getSequence");
    }

    /**
     * Test of getRegions method, of class MappingCtx.
     */
    @Test
    public void testGetRegions() {
        System.out.println("getRegions");

    }

    @Test
    public void testGetMappedReadCount() {
        System.out.println("testGetMappedReadCount");
        MGXMasterI master = TestMaster.getRO();
        Iterator<MappingI> iter = master.Mapping().fetchall();
        int cnt = 0;
        MappingI mapping = null;
        while (iter.hasNext()) {
            mapping = iter.next();
            cnt++;
        }
        assertEquals(1, cnt);
        assertNotNull(mapping);
        assertEquals(30, mapping.getId());

        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
        JobI job = master.Job().fetch(mapping.getJobID());
        MappingCtx ctx = new MappingCtx(mapping, ref, job);
        
        SortedSet<MappedSequenceI> mappings = ctx.getMappings(0, ref.getLength()-1);
        // $ samtools view 124.bam | wc -l
        // 405
        assertEquals(405, mappings.size());
    }

    /**
     * Test of getMappings method, of class MappingCtx.
     */
    @Test
    public void testGetMappings() {
        System.out.println("getMappings");
        MGXMasterI master = TestMaster.getRO();
        Iterator<MappingI> iter = master.Mapping().fetchall();
        int cnt = 0;
        MappingI mapping = null;
        while (iter.hasNext()) {
            mapping = iter.next();
            cnt++;
        }
        assertEquals(1, cnt);
        assertNotNull(mapping);

        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
        JobI job = master.Job().fetch(mapping.getJobID());

        UUID uuid = master.Mapping().openMapping(mapping.getId());

        MappingCtx ctx = new MappingCtx(mapping, ref, job);

        SortedSet<MappedSequenceI> mappings = ctx.getMappings(6385, 6395);
        master.Mapping().closeMapping(uuid);

        for (MappedSequenceI ms : mappings) {
            System.err.println(ms.getStart() + " - " + ms.getStop());
        }
        assertEquals(1, mappings.size());
    }

    /**
     * Test of getCoverage method, of class MappingCtx.
     */
//    @Test
//    public void testGetCoverage() {
//        System.out.println("getCoverage");
//        MGXMaster master = TestMaster.getRO();
//        Iterator<Mapping> iter = master.Mapping().fetchall();
//        int cnt = 0;
//        Mapping mapping = null;
//        while (iter.hasNext()) {
//            mapping = iter.next();
//            cnt++;
//        }
//        assertEquals(1, cnt);
//        assertNotNull(mapping);
//
//        Reference ref = master.Reference().fetch(mapping.getReferenceID());
//        Job job = master.Job().fetch(mapping.getJobID());
//
//        UUID uuid = master.Mapping().openMapping(mapping.getId());
//
//        MappingCtx ctx = new MappingCtx(mapping, ref, job);
//
//        int[] mappings = new int[6392-6389+1];
//        ctx.getCoverage(6389, 6392, mappings);
//        assertEquals(4, mappings.length);
//        for (int i : mappings) {
//            System.err.print(i + " ");
//        }
//
//        assertEquals(0, mappings[0]); // 6389
//        assertEquals(0, mappings[1]); // 6390
//        assertEquals(1, mappings[2]); // 6391
//        assertEquals(1, mappings[3]); // 6392
//
//        master.Mapping().closeMapping(uuid);
//    }
//    @Test
//    public void testGetCoverageIterator() {
//        System.out.println("getCoverageIterator");
//        MGXMaster master = TestMaster.getRO();
//        Iterator<Mapping> iter = master.Mapping().fetchall();
//        int cnt = 0;
//        Mapping mapping = null;
//        while (iter.hasNext()) {
//            mapping = iter.next();
//            cnt++;
//        }
//        assertEquals(1, cnt);
//        assertNotNull(mapping);
//
//        Reference ref = master.Reference().fetch(mapping.getReferenceID());
//        Job job = master.Job().fetch(mapping.getJobID());
//
//        UUID uuid = master.Mapping().openMapping(mapping.getId());
//
//        MappingCtx ctx = new MappingCtx(mapping, ref, job);
//
//        IntIterator intIter = ctx.getCoverageIterator(6389, 6392);
//        assertNotNull(intIter);
//
//        int poscnt = 0;
//        int[] mappings = new int[4];
//        while (intIter.hasNext()) {
//            //System.err.println(intIter.next());
//            mappings[poscnt] = intIter.next();
//            poscnt++;
//        }
//
//        assertEquals(4, poscnt);
//
//        assertEquals(0, mappings[0]); // 6389
//        assertEquals(0, mappings[1]); // 6390
//        assertEquals(1, mappings[2]); // 6391
//        assertEquals(1, mappings[3]); // 6392
//
//        master.Mapping().closeMapping(uuid);
//    }
}
