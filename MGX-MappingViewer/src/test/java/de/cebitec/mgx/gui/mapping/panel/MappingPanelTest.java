/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.mapping.panel;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.Mapping;
import de.cebitec.mgx.gui.datamodel.Reference;
import de.cebitec.mgx.gui.mapping.MappingCtx;
import de.cebitec.mgx.gui.mapping.TestMaster;
import de.cebitec.mgx.gui.mapping.ViewController;
import java.util.Iterator;
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
public class MappingPanelTest {

    public MappingPanelTest() {
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
    public void testTiming() {
        System.out.println("testTiming");
        MGXMaster master = TestMaster.getRO();
        Iterator<Mapping> iter = master.Mapping().fetchall();
        int cnt = 0;
        Mapping mapping = null;
        while (iter.hasNext()) {
            mapping = iter.next();
            cnt++;
        }
        Reference ref = master.Reference().fetch(mapping.getReferenceID());
        Job job = master.Job().fetch(mapping.getJobID());
        UUID uuid = master.Mapping().openMapping(mapping.getId());
        MappingCtx ctx = new MappingCtx(mapping, ref, job);
        ViewController vc = new ViewController(ctx);
        
        vc.setBounds(0, ref.getLength()-1);
        vc.getMappings(0, ref.getLength()-1);
        
        MappingPanel mp = new MappingPanel(vc);
        mp.setSize(768, 77);
        
        for (int i = 0; i < 10; i++) {
            mp.update();
        }
    }

}
