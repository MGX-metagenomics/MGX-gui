/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
public class ViewControllerTest {

    public ViewControllerTest() {
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

//    @Test
//    public void testMappingOrder() {
//        System.err.println("testMappingOrder");
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
//        MappingCtx ctx = new MappingCtx(mapping, ref, job);
//        ViewController vc = new ViewController(ctx);
//        
//        SortedSet<MappedSequenceI> mappings = vc.getMappings(0, ref.getLength()-1);
//        assertTrue(mappings.size() > 0);
//        for (MappedSequenceI ms : mappings) {
//            System.err.println(ms.getIdentity());
//        }
//    }

}
