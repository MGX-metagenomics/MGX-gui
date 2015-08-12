///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.cebitec.mgx.gui.mapping.panel;
//
//import de.cebitec.mgx.api.MGXMasterI;
//import de.cebitec.mgx.api.exception.MGXException;
//import de.cebitec.mgx.api.model.JobI;
//import de.cebitec.mgx.api.model.MGXReferenceI;
//import de.cebitec.mgx.api.model.MappingI;
//import de.cebitec.mgx.gui.mapping.MappingCtx;
//import de.cebitec.mgx.gui.mapping.TestMaster;
//import de.cebitec.mgx.gui.mapping.ViewController;
//import java.util.Iterator;
//import java.util.UUID;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
///**
// *
// * @author sj
// */
//public class MappingPanelTest {
//
//    public MappingPanelTest() {
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
//    public void testTiming() throws MGXException {
//        System.out.println("testTiming");
//        MGXMasterI master = TestMaster.getRO();
//        Iterator<MappingI> iter = master.Mapping().fetchall();
//        MappingI mapping = null;
//        while (iter.hasNext()) {
//            mapping = iter.next();
//        }
//        MGXReferenceI ref = master.Reference().fetch(mapping.getReferenceID());
//        JobI job = master.Job().fetch(mapping.getJobID());
//        UUID uuid = master.Mapping().openMapping(mapping.getId());
//        MappingCtx ctx = new MappingCtx(mapping, ref, job, master.SeqRun().fetch(mapping.getSeqrunID()));
//        ViewController vc = new ViewController(ctx);
//        
//        vc.setBounds(0, ref.getLength()-1);
//        //vc.getMappings(0, ref.getLength()-1);
//        
//        MappingPanel mp = new MappingPanel(vc, null);
//        mp.setSize(768, 77);
//        
////        for (int i = 0; i < 10; i++) {
////            mp.update();
////        }
//    }
//
//}
