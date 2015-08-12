///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.gui.mapping.tracks;
//
//import de.cebitec.mgx.gui.datamodel.MappedSequence;
//import java.util.Iterator;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import static org.junit.Assert.*;
//
///**
// *
// * @author sjaenick
// */
//public class TrackTest {
//
//    public TrackTest() {
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
////    public void testOverlap() {
////        System.out.println("overlap");
////        Track t = new Track();
////
////        MappedSequence ms1 = new MappedSequence(1, 5, 20, 0);
////        MappedSequence ms2 = new MappedSequence(1, 15, 25, 0);
////
////        assertFalse(t.overlaps(ms1));
////
////        t.add(ms1);
////
////        assertTrue(t.overlaps(ms2));
////    }
//
////    @Test
////    public void testOverlap2() {
////        System.out.println("overlap2");
////        Track t = new Track();
////
////        MappedSequence ms1 = new MappedSequence(1, 5, 20, 0);
////        MappedSequence ms3 = new MappedSequence(2, 25, 45, 0);
////
////        t.add(ms1);
////
////        assertFalse(t.overlaps(ms3));
////    }
//
////    @Test
////    public void testOverlaps() {
////        System.out.println("overlaps");
////        MappedSequence ms = null;
////        Track instance = new Track();
////        boolean expResult = false;
////        boolean result = instance.overlaps(ms);
////        assertEquals(expResult, result);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
////    }
////
////    @Test
////    public void testGetVOffset() {
////        System.out.println("getVOffset");
////        Track instance = new Track();
////        double expResult = 0.0;
////        double result = instance.getVOffset();
////        assertEquals(expResult, result, 0.0);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
////    }
////
////    @Test
////    public void testGetSequences() {
////        System.out.println("getSequences");
////        Track instance = new Track();
////        Iterator<MappedSequence> expResult = null;
////        Iterator<MappedSequence> result = instance.getSequences();
////        assertEquals(expResult, result);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
////    }
//}
