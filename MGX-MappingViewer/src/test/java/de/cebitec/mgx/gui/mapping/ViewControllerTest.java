///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
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
//import de.cebitec.mgx.gui.cache.IntIterator;
//import java.util.Iterator;
//import java.util.SortedSet;
//import org.junit.After;
//import org.junit.AfterClass;
//import static org.junit.Assert.*;
//import org.junit.Assume;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
///**
// *
// * @author sj
// */
//public class ViewControllerTest {
//
//    public ViewControllerTest() {
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
////    @Test
////    public void testCoverage() throws MGXException {
////        System.err.println("testCoverage");
////        MGXMasterI master = TestMaster.getRO();
////        Iterator<MappingI> iter = master.Mapping().fetchall();
////        int cnt = 0;
////        MappingI mapping = null;
////        while (iter.hasNext()) {
////            mapping = iter.next();
////            cnt++;
////        }
////        assertEquals(1, cnt);
////        assertNotNull(mapping);
////        assertEquals(30, mapping.getId());
////        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
////        JobI job = master.Job().fetch(mapping.getJobID());
////        MappingCtx ctx = new MappingCtx(mapping, ref, job, master.SeqRun().fetch(mapping.getSeqrunID()));
////        ViewController vc = new ViewController(ctx);
////        //
////        //
////        int max = -1;
////        int numPos = 0;
////        IntIterator covIter = vc.getCoverageIterator();
////        while (covIter.hasNext()) {
////            int c = covIter.next();
////            numPos++;
////            if (c > max) {
////                max = c;
////            }
////        }
////        assertEquals(numPos, ref.getLength());
////        long maxCov = vc.getMaxCoverage();
////        assertEquals(maxCov, max);
////    }
//
////    @Test
////    public void testCoveragePriv() throws MGXException {
////        System.err.println("testCoveragePriv");
////        MGXMasterI master = TestMaster.getPrivate();
////        Assume.assumeNotNull(master);
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
////        ViewController vc = new ViewController(new MappingCtx(mapping, ref, job, master.SeqRun().fetch(mapping.getSeqrunID())));
////        //
////        //
////        int max = -1;
////        int numPos = 0;
////        IntIterator covIter = vc.getCoverageIterator();
////        while (covIter.hasNext()) {
////            int c = covIter.next();
////            numPos++;
////            if (c > max) {
////                max = c;
////            }
////        }
////        assertEquals(numPos, ref.getLength());
////        long maxCov = vc.getMaxCoverage();
////        assertEquals(maxCov, max);
////    }
//
////    @Test
////    public void testMappingsPrivMax() throws MGXException {
////        System.err.println("testMappingsPrivMax");
////        MGXMasterI master = TestMaster.getPrivate();
////        Assume.assumeNotNull(master);
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
////        MappingCtx ctx = new MappingCtx(mapping, ref, job, master.SeqRun().fetch(mapping.getSeqrunID()));
////        ViewController vc = new ViewController(ctx);
////        //
////        //
////        SortedSet<MappedSequenceI> mappings = vc.getMappings(567083, 567083);
////        assertNotNull(mappings);
////        assertEquals(8901, mappings.size());
////    }
//
////    @Test
////    public void testMappingsPriv() throws MGXException {
////        System.err.println("testMappingsPriv");
////        MGXMasterI master = TestMaster.getPrivate();
////        Assume.assumeNotNull(master);
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
////        MappingCtx ctx = new MappingCtx(mapping, ref, job, master.SeqRun().fetch(mapping.getSeqrunID()));
////        ViewController vc = new ViewController(ctx);
////        //
////        //
////        SortedSet<MappedSequenceI> mappings = vc.getMappings(0, ref.getLength() - 1);
////        assertNotNull(mappings);
////        assertEquals(21883, mappings.size());
////
////        for (MappedSequenceI ms : mappings) {
////            if (ms.getSeqId() == 6520119) {
////                assertEquals(384, ms.getStop());
////                assertEquals(524, ms.getStart());
////            }
////        }
////    }
//
////    @Test
////    public void testMappingOrder() {
////        System.err.println("testMappingOrder");
////        MGXMasterI master = TestMaster.getRO();
////        Iterator<MappingI> iter = master.Mapping().fetchall();
////        int cnt = 0;
////        MappingI mapping = null;
////        while (iter.hasNext()) {
////            mapping = iter.next();
////            cnt++;
////        }
////        assertEquals(1, cnt);
////        assertNotNull(mapping);
////        assertEquals(30, mapping.getId());
////
////        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
////        JobI job = master.Job().fetch(mapping.getJobID());
////        MappingCtx ctx = new MappingCtx(mapping, ref, job);
////        ViewController vc = new ViewController(ctx);
////        
////        SortedSet<MappedSequenceI> mappings = vc.getMappings(0, ref.getLength()-1);
////        assertTrue(mappings.size() > 0);
////        for (MappedSequenceI ms : mappings) {
////            System.err.println(ms.getIdentity());
////        }
////    }
//}
