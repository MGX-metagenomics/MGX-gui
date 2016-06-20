/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.SequenceI;
import de.cebitec.mgx.gui.util.TestMaster;
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
public class SequenceAccessTest {

    public SequenceAccessTest() {
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
    public void testFetch_SeqRunI_String() throws Exception {
        System.out.println("testFetch_SeqRunI_String");
        MGXMasterI master = TestMaster.getRO();
        SeqRunI seqrun = master.SeqRun().fetch(1);
        assertNotNull(seqrun);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            SequenceI seq = master.Sequence().fetch(seqrun, "FI5LW4G01AM15A");
            assertNotNull(seq);
        }
        start = System.currentTimeMillis() - start;
        System.err.println("duration in ms: "+ start);
    }
}
