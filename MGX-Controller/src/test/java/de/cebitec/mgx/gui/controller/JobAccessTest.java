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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
public class JobAccessTest {

    public JobAccessTest() {
    }

    @Test
    public void testFetch() throws Exception {
        System.out.println("fetch");
        MGXMasterI master = TestMaster.getRO();
        JobI job = master.Job().fetch(1);
        assertNotNull(job);
        assertEquals("Thu Jun 20 15:19:18 CEST 2013", job.getStartDate().toString());
        assertEquals("Thu Jun 20 15:20:01 CEST 2013", job.getFinishDate().toString());
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
        assertNotSame(j1, j2);
        assertEquals(j1, j2);
        assertNotEquals(j1, j3);
    }
}
