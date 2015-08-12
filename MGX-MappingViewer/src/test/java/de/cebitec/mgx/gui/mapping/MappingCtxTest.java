///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.gui.mapping;
//
//import de.cebitec.mgx.api.MGXMasterI;
//import de.cebitec.mgx.api.exception.MGXException;
//import de.cebitec.mgx.api.model.JobI;
//import de.cebitec.mgx.api.model.MGXReferenceI;
//import de.cebitec.mgx.api.model.MappedSequenceI;
//import de.cebitec.mgx.api.model.MappingI;
//import de.cebitec.mgx.api.model.SeqRunI;
//import de.cebitec.mgx.gui.cache.IntIterator;
//import java.util.Iterator;
//import java.util.SortedSet;
//import java.util.UUID;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import org.junit.Assume;
//import org.openide.util.Exceptions;
//
///**
// *
// * @author sj
// */
//public class MappingCtxTest {
//
//    public MappingCtxTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() {
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//    }
//
//    @Before
//    public void setUp() {
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    @Test
//    public void testGetMappedReadCount() throws MGXException {
//        System.out.println("testGetMappedReadCount");
//        MGXMasterI master = TestMaster.getRO();
//        Iterator<MappingI> iter = master.Mapping().fetchall();
//        int cnt = 0;
//        MappingI mapping = null;
//        while (iter.hasNext()) {
//            mapping = iter.next();
//            cnt++;
//        }
//        assertEquals(1, cnt);
//        assertNotNull(mapping);
//        assertEquals(30, mapping.getId());
//
//        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
//        JobI job = master.Job().fetch(mapping.getJobID());
//        job.setSeqrun(master.SeqRun().fetch(mapping.getSeqrunID()));
//        MappingCtx ctx = new MappingCtx(mapping, ref, job, job.getSeqrun());
//
//        SortedSet<MappedSequenceI> mappings = ctx.getMappings(0, ref.getLength() - 1);
//        // $ samtools view 124.bam | wc -l
//        // 405
//        assertEquals(405, mappings.size());
//    }
//
//    /**
//     * Test of getMappings method, of class MappingCtx.
//     */
//    @Test
//    public void testGetMappings() throws MGXException {
//        System.out.println("getMappings");
//        MGXMasterI master = TestMaster.getRO();
//        Iterator<MappingI> iter = master.Mapping().fetchall();
//        int cnt = 0;
//        MappingI mapping = null;
//        while (iter.hasNext()) {
//            mapping = iter.next();
//            cnt++;
//        }
//        assertEquals(1, cnt);
//        assertNotNull(mapping);
//
//        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
//        JobI job = master.Job().fetch(mapping.getJobID());
//        job.setSeqrun(master.SeqRun().fetch(mapping.getSeqrunID()));
//
//        UUID uuid = master.Mapping().openMapping(mapping.getId());
//
//        MappingCtx ctx = new MappingCtx(mapping, ref, job, job.getSeqrun());
//
//        SortedSet<MappedSequenceI> mappings = ctx.getMappings(6385, 6395);
//        master.Mapping().closeMapping(uuid);
//
//        for (MappedSequenceI ms : mappings) {
//            System.err.println(ms.getStart() + " - " + ms.getStop());
//        }
//        assertEquals(1, mappings.size());
//    }
//
////    @Test
////    public void testMappingsPrivMax() throws MGXException {
////        System.err.println("testMappingsPrivMax");
////        MGXMasterI master = TestMaster.getPrivate();
////        if (master == null) {
////            System.err.println("    private test, skipped");
////            return;
////        }
////        Iterator<MappingI> iter = master.Mapping().fetchall();
////        int cnt = 0;
////        MappingI mapping = null;
////        while (iter.hasNext()) {
////            mapping = iter.next();
////            cnt++;
////        }
////        assertEquals(1, cnt);
////        assertNotNull(mapping);
////        assertEquals(1, mapping.getId());
////        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
////        JobI job = master.Job().fetch(mapping.getJobID());
////        MappingCtx ctx = new MappingCtx(mapping, ref, job, job.getSeqrun());
////        //
////        //
////        SortedSet<MappedSequenceI> mappings = ctx.getMappings(567083, 567083);
////        assertNotNull(mappings);
////        assertEquals(8901, mappings.size());
////    }
////    @Test
////    public void testMaxCovPriv() throws MGXException {
////        System.err.println("testMaxCovPriv");
////        MGXMasterI master = TestMaster.getPrivate();
////        if (master == null) {
////            System.err.println("    private test, skipped");
////            return;
////        }
////        Iterator<MappingI> iter = master.Mapping().fetchall();
////        MappingI mapping = null;
////        while (iter.hasNext()) {
////            mapping = iter.next();
////        }
////        assertNotNull(mapping);
////        assertEquals(1, mapping.getId());
////        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
////        JobI job = master.Job().fetch(mapping.getJobID());
////        MappingCtx ctx = new MappingCtx(mapping, ref, job, job.getSeqrun());
////        //
////        //
////        long maxCoverage = ctx.getMaxCoverage();
////        assertEquals(8901, maxCoverage);
////    }
////    @Test
////    public void testCoveragePriv() throws MGXException {
////        System.err.println("testCoveragePriv");
////        MGXMasterI master = TestMaster.getPrivate();
////        if (master == null) {
////            System.err.println("    private test, skipped");
////            return;
////        }
////        Iterator<MappingI> iter = master.Mapping().fetchall();
////        int cnt = 0;
////        MappingI mapping = null;
////        while (iter.hasNext()) {
////            mapping = iter.next();
////            cnt++;
////        }
////        assertEquals(1, cnt);
////        assertNotNull(mapping);
////        assertEquals(1, mapping.getId());
////        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
////        JobI job = master.Job().fetch(mapping.getJobID());
////        MappingCtx ctx = new MappingCtx(mapping, ref, job, job.getSeqrun());
////        //
////        //
////        int max = -1;
////        int numPos = 0;
////        IntIterator covIter = ctx.getCoverageIterator(0, ref.getLength() - 1);
////        while (covIter.hasNext()) {
////            int c = covIter.next();
////            numPos++;
////            if (c > max) {
////                max = c;
////            }
////        }
////        assertEquals(numPos, ref.getLength());
////        assertEquals(8901, max);
////    }
//    @Test
//    public void testFetch() {
//        System.out.println("testFetch");
//        MGXMasterI master = TestMaster.getRO();
//        MappingI mapping = null;
//        try {
//            mapping = master.Mapping().fetch(30);
//        } catch (MGXException ex) {
//            fail(ex.getMessage());
//        }
//        assertNotNull(mapping);
//        assertEquals(30, mapping.getId());
//        assertEquals(124, mapping.getJobID());
//        assertEquals(1, mapping.getSeqrunID());
//        assertEquals(8, mapping.getReferenceID());
//    }
//
//    @Test
//    public void testInvalidID() {
//        System.out.println("testInvalidID");
//        MGXMasterI master = TestMaster.getRO();
//        MappingI mapping = null;
//        try {
//            mapping = master.Mapping().fetch(15);
//        } catch (MGXException ex) {
//            if (ex.getMessage().contains("No object of type Mapping for ID 15.")) {
//                return;
//            }
//            fail(ex.getMessage());
//        }
//        assertNull(mapping);
//    }
//
//    /**
//     * Test of getCoverage method, of class MappingCtx.
//     */
//    @Test
//    public void testGetCoverage() throws MGXException {
//        System.out.println("getCoverage");
//        MGXMasterI master = TestMaster.getRO();
//        Iterator<MappingI> iter = master.Mapping().fetchall();
//        int cnt = 0;
//        MappingI mapping = null;
//        while (iter.hasNext()) {
//            mapping = iter.next();
//            cnt++;
//        }
//        assertEquals(1, cnt);
//        assertNotNull(mapping);
//
//        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
//        JobI job = master.Job().fetch(mapping.getJobID());
//        SeqRunI run = master.SeqRun().fetch(mapping.getSeqrunID());
//
//        UUID uuid = master.Mapping().openMapping(mapping.getId());
//
//        MappingCtx ctx = new MappingCtx(mapping, ref, job, run);
//
//        long maxCov = ctx.getMaxCoverage();
//        assertEquals(3, maxCov);
//
//        IntIterator intiter = ctx.getCoverageIterator(0, ctx.getReference().getLength() - 1);
//        assertNotNull(intiter);
//
//        int numPos = 0;
//        long cov = -1;
//        while (intiter.hasNext()) {
//            int curCov = intiter.next();
//            if (curCov > cov) {
//                cov = curCov;
//            }
//            numPos++;
//        }
//        assertEquals(3, cov);
//
//        assertEquals(ctx.getReference().getLength(), numPos);
//        master.Mapping().closeMapping(uuid);
//    }
//
//    @Test
//    public void testGetCoveragePrivate() throws MGXException {
//        System.out.println("testGetCoveragePrivate");
//        MGXMasterI master = TestMaster.getPrivate("MGX_devel");
//        Assume.assumeNotNull(master);
//        
//        MappingI mapping = master.Mapping().fetch(15);
//        assertNotNull(mapping);
//
//        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
//        JobI job = master.Job().fetch(mapping.getJobID());
//        SeqRunI run = master.SeqRun().fetch(mapping.getSeqrunID());
//
//        UUID uuid = master.Mapping().openMapping(mapping.getId());
//
//        MappingCtx ctx = new MappingCtx(mapping, ref, job, run);
//        long maxCov = ctx.getMaxCoverage();
//        assertEquals(526, maxCov);
//
//        IntIterator intiter = ctx.getCoverageIterator(0, ctx.getReference().getLength() - 1);
//        assertNotNull(intiter);
//
//        int numPos = 0;
//        long cov = -1;
//        while (intiter.hasNext()) {
//            int curCov = intiter.next();
//            if (curCov > cov) {
//                cov = curCov;
//            }
//            numPos++;
//        }
//        assertEquals(526, cov);
//
//        assertEquals(ctx.getReference().getLength(), numPos);
//        master.Mapping().closeMapping(uuid);
//    }
//
////    @Test
////    public void testGetCoverageIterator() {
////        System.out.println("getCoverageIterator");
////        MGXMaster master = TestMaster.getRO();
////        Iterator<Mapping> iter = master.Mapping().fetchall();
////        int cnt = 0;
////        Mapping mapping = null;
////        while (iter.hasNext()) {
////            mapping = iter.next();
////            cnt++;
////        }
////        assertEquals(1, cnt);
////        assertNotNull(mapping);
////
////        Reference ref = master.Reference().fetch(mapping.getReferenceID());
////        Job job = master.Job().fetch(mapping.getJobID());
////
////        UUID uuid = master.Mapping().openMapping(mapping.getId());
////
////        MappingCtx ctx = new MappingCtx(mapping, ref, job);
////
////        IntIterator intIter = ctx.getCoverageIterator(6389, 6392);
////        assertNotNull(intIter);
////
////        int poscnt = 0;
////        int[] mappings = new int[4];
////        while (intIter.hasNext()) {
////            //System.err.println(intIter.next());
////            mappings[poscnt] = intIter.next();
////            poscnt++;
////        }
////
////        assertEquals(4, poscnt);
////
////        assertEquals(0, mappings[0]); // 6389
////        assertEquals(0, mappings[1]); // 6390
////        assertEquals(1, mappings[2]); // 6391
////        assertEquals(1, mappings[3]); // 6392
////
////        master.Mapping().closeMapping(uuid);
////    }
//}
