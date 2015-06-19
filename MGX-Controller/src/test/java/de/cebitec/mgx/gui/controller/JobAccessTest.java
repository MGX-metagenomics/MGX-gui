/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class JobAccessTest {

    public JobAccessTest() {
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
    public void testBySeqRun() throws Exception {
        System.out.println("BySeqRun");
        MGXMasterI master = TestMaster.getRO();

        SeqRunI run = master.SeqRun().fetch(1);
        List<JobI> jobs = master.Job().BySeqRun(run);
        assertEquals(10, jobs.size());
    }

    @Test
    public void testEquals() throws Exception {
        System.out.println("testEquals");
        MGXMasterI master = TestMaster.getRO();
        JobI j1 = master.Job().fetch(1);
        JobI j2 = master.Job().fetch(1);
        JobI j3 = master.Job().fetch(2);
        assertEquals(j1, j2);
        assertNotEquals(j1, j3);
    }
}
