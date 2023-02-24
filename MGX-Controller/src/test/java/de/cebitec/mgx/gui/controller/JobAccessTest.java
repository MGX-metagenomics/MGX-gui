/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.ToolI;
import de.cebitec.mgx.testutils.PropCounter;
import de.cebitec.mgx.testutils.TestMaster;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

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
        JobI job = master.Job().fetch(9);
        assertNotNull(job);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        assertEquals("2023-02-10T10:13:32Z", sdf.format(job.getStartDate()));
        assertEquals("2023-02-10T10:21:33Z", sdf.format(job.getFinishDate()));
        
        assertNotNull(job.getSeqruns());
        assertEquals(1, job.getSeqruns().length);
    }

    @Test
    public void testFetchall() throws MGXException {
        System.out.println("fetchall");
        MGXMasterI m = TestMaster.getRO();
        Iterator<JobI> iter = m.Job().fetchall();
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            JobI job = iter.next();
            assertNotNull(job);
            
            if (job.getAssembly() != null) {
                assertNull(job.getSeqruns());
            }
            if (job.getSeqruns() != null) {
                assertNull(job.getAssembly());
            }
            cnt++;
        }
        assertEquals(6, cnt);
    }

    @Test
    public void testBySeqRun() throws Exception {
        System.out.println("BySeqRun");
        MGXMasterI master = TestMaster.getRO();

        SeqRunI run = master.SeqRun().fetch(49);
        List<JobI> jobs = master.Job().BySeqRun(run);
        assertEquals(4, jobs.size());
    }

    @Test
    public void testDeletion() throws Exception {
        System.out.println("deletion");
        MGXMasterI master = TestMaster.getRW();
        ToolI tool = master.Tool().fetch(17);
        SeqRunI run = master.SeqRun().fetch(49);

        JobI job = master.Job().create(tool, new ArrayList<>(), run);
        assertNotNull(job);

        PropCounter pc = new PropCounter();

        job.addPropertyChangeListener(pc);

        TaskI<JobI> delTask = master.Job().delete(job);
        while (!delTask.done()) {
            master.<JobI>Task().refresh(delTask);
        }
        assertTrue(delTask.done());
        assertTrue(job.isDeleted());

        assertEquals(1, pc.getCount());
        assertEquals(JobI.OBJECT_DELETED, pc.getLastEvent().getPropertyName());
    }

    @Test
    public void testEquals() throws Exception {
        System.out.println("testEquals");
        MGXMasterI master = TestMaster.getRO();
        JobI j1 = master.Job().fetch(7);
        JobI j2 = master.Job().fetch(7);
        JobI j3 = master.Job().fetch(8);
        //assertNotSame(j1, j2);
        assertEquals(j1, j2);
        assertNotEquals(j1, j3);
    }
}
