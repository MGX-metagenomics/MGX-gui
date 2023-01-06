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
        JobI job = master.Job().fetch(1);
        assertNotNull(job);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        assertEquals("2013-06-20T13:19:18Z", sdf.format(job.getStartDate()));
        assertEquals("2013-06-20T13:20:01Z", sdf.format(job.getFinishDate()));
    }

    @Test
    public void testFetchall() throws MGXException {
        System.out.println("fetchall");
        MGXMasterI m = TestMaster.getRO();
        Iterator<JobI> iter = m.Job().fetchall();
        assertNotNull(iter);
        int cnt = 0;
        while (iter.hasNext()) {
            JobI next = iter.next();
            assertNotNull(next);
            cnt++;
        }
        assertEquals(15, cnt);
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
    public void testBySeqRun_mgx2() throws Exception {
        System.out.println("BySeqRun");
        MGXMasterI master = TestMaster.getPrivate("MGX2_devel");
        assertNotNull(master);
        SeqRunI run = master.SeqRun().fetch(1);
        assertNotNull(run);
        List<JobI> jobs = master.Job().BySeqRun(run);
        assertEquals(2, jobs.size());
    }

    @Test
    public void testDeletion() throws Exception {
        System.out.println("deletion");
        MGXMasterI master = TestMaster.getRW();
        ToolI tool = master.Tool().fetch(1);
        SeqRunI run = master.SeqRun().fetch(1);

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
        JobI j1 = master.Job().fetch(1);
        JobI j2 = master.Job().fetch(1);
        JobI j3 = master.Job().fetch(2);
        //assertNotSame(j1, j2);
        assertEquals(j1, j2);
        assertNotEquals(j1, j3);
    }
}
