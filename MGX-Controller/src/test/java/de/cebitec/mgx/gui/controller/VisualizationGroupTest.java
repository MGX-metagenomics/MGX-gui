/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.JobState;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.api.visualization.ConflictResolver;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author sjaenick
 */
public class VisualizationGroupTest {

    @Test
    public void testSetName() {
        System.out.println("setName");
        VisualizationGroupI vg = VGroupManager.getTestInstance().createVisualizationGroup();
        String name = UUID.randomUUID().toString();
        vg.setName(name);
        assertEquals(name, vg.getDisplayName());
    }

    @Test
    public void testGetNumSequences() throws Exception {
        System.out.println("getNumSequences");

        VGroupManagerI vgmgr = VGroupManager.getTestInstance();
        synchronized (vgmgr) {
            VisualizationGroupI vg = vgmgr.createVisualizationGroup();
            MGXMasterI master = TestMaster.getRO();
            Iterator<SeqRunI> iter = master.SeqRun().fetchall();
            long cnt = 0;
            while (iter.hasNext()) {
                SeqRunI sr = iter.next();
                vg.addSeqRun(sr);
                cnt += sr.getNumSequences();
            }
            assertEquals(cnt, vg.getNumSequences());
        }
    }

    @Test
    public void testGetSeqRuns() throws Exception {
        System.out.println("getSeqRuns");
        VGroupManagerI vgmgr = VGroupManager.getTestInstance();
        synchronized (vgmgr) {
            VisualizationGroupI vg = vgmgr.createVisualizationGroup();
            MGXMasterI master = TestMaster.getRO();
            Set<SeqRunI> runs = new HashSet<>();
            Iterator<SeqRunI> iter = master.SeqRun().fetchall();
            while (iter.hasNext()) {
                SeqRunI sr = iter.next();
                vg.addSeqRun(sr);
                runs.add(sr);
            }
            assertEquals(runs, vg.getSeqRuns());
        }
    }

    @Test
    public void testGetAttributeTypes() throws Exception {
        System.out.println("getAttributeTypes");
        VGroupManagerI vgmgr = VGroupManager.getTestInstance();
        synchronized (vgmgr) {
            VisualizationGroupI vg = vgmgr.createVisualizationGroup();
            MGXMasterI master = TestMaster.getRO();
            Iterator<SeqRunI> iter = master.SeqRun().fetchall();
            while (iter.hasNext()) {
                vg.addSeqRun(iter.next());
            }
            Iterator<AttributeTypeI> atIter = vg.getAttributeTypes();
            assertNotNull(atIter);
            int cnt = 0;
            while (atIter.hasNext()) {
                AttributeTypeI next = atIter.next();
                //System.err.println("  " + next.getName());
                cnt++;
            }
            assertEquals(22, cnt);
        }
    }

    @Test
    public void testaddAndRemoveRuns() throws Exception {
        System.out.println("addAndRemoveRuns");
        VGroupManagerI vgmgr = VGroupManager.getTestInstance();
        VisualizationGroupI vg = vgmgr.createVisualizationGroup();
        MGXMasterI master = TestMaster.getRO();

        Iterator<SeqRunI> iter = master.SeqRun().fetchall();
        SeqRunI run = null;
        while (iter.hasNext()) {
            run = iter.next();
            if (run.getName().equals("dataset2")) {
                break;
            }
        }
        vg.addSeqRun(run);
        Iterator<AttributeTypeI> atIter = vg.getAttributeTypes();
        assertNotNull(atIter);
        int cnt = 0;
        while (atIter.hasNext()) {
            AttributeTypeI next = atIter.next();
            cnt++;
        }
        assertEquals(15, cnt);

        // remove run again
        vg.removeSeqRun(run);
        Iterator<AttributeTypeI> atIter2 = vg.getAttributeTypes();
        assertNotNull(atIter);
        int cnt2 = 0;
        while (atIter2.hasNext()) {
            AttributeTypeI next = atIter2.next();
            cnt2++;
        }
        assertEquals(0, cnt2);
    }

    @Test
    public void testGetSelectedAttributeTypeRegression() throws Exception {
        System.out.println("getSelectedAttributeTypeRegression");

        VGroupManagerI vgmgr = VGroupManager.getTestInstance();
        VisualizationGroupI vg = vgmgr.createVisualizationGroup();
        MGXMasterI master = TestMaster.getRO();
        SeqRunI run = master.SeqRun().fetch(1);
        assertEquals("dataset1", run.getName());
        vg.addSeqRun(run);
        assertEquals(1, vg.getSeqRuns().size());
        Iterator<AttributeTypeI> atIter = vg.getAttributeTypes();
        assertNotNull(atIter);

        try {
            vg.selectAttributeType(AttributeRank.PRIMARY, "NCBI_SUPERKINGDOM");
        } catch (ConflictingJobsException ex) {
            Map<SeqRunI, Set<JobI>> conflicts = vg.getConflicts(AttributeRank.PRIMARY);
            assertEquals(1, conflicts.size());
            Set<JobI> jobs = conflicts.get(run);
            assertEquals(2, jobs.size());
            for (JobI j : jobs) {
                assertEquals(JobState.FINISHED, j.getStatus());
            }
            return;
        }
        fail();
    }

    @Test
    public void testGetSelectedAttributeType() throws Exception {
        System.out.println("getSelectedAttributeType");
        VGroupManagerI vgmgr = VGroupManager.getTestInstance();
        VisualizationGroupI vg = vgmgr.createVisualizationGroup();
        MGXMasterI master = TestMaster.getRO();
        Iterator<SeqRunI> iter = master.SeqRun().fetchall();
        while (iter.hasNext()) {
            vg.addSeqRun(iter.next());
        }
        assertEquals(4, vg.getSeqRuns().size());

        int atCount = 0;
        Iterator<AttributeTypeI> atIter = vg.getAttributeTypes();
        assertNotNull(atIter);
        while (atIter.hasNext()) {
            atIter.next();
            atCount++;
        }
        assertEquals(22, atCount);

        try {
            vg.selectAttributeType(AttributeRank.PRIMARY, "NCBI_SUPERKINGDOM");
        } catch (ConflictingJobsException ex) {
            Map<SeqRunI, Set<JobI>> conflicts = vg.getConflicts(AttributeRank.PRIMARY);
            assertEquals(2, conflicts.size());
            return;
        }
        fail();
    }

    @Test
    public void testSelectedAttributeAfterConflict() throws MGXException {
        System.out.println("testSelectedAttributeAfterConflict");
        VGroupManagerI mgr = VGroupManager.getTestInstance();

        MGXMasterI master = TestMaster.getRO();
        SeqRunI dataset1 = master.SeqRun().fetch(1);
        assertEquals("dataset1", dataset1.getName());
        VisualizationGroupI vGrp = mgr.createVisualizationGroup();
        vGrp.addSeqRun(dataset1);

        boolean haveException = false;
        try {
            vGrp.selectAttributeType(AttributeRank.PRIMARY, "NCBI_PHYLUM");
        } catch (ConflictingJobsException ex) {
            haveException = true;
        }
        assertTrue("Job conflict exception should have been thrown.", haveException);
        assertNull("attribute type should be null if a conflict occurs", vGrp.getSelectedAttributeType());
    }

    @Test
    public void testReplicateGroupRegression() throws Exception {
        System.out.println("testReplicateGroupRegression");
        VGroupManagerI vgmgr = VGroupManager.getTestInstance();
        SeqRunI seqrun = TestMaster.getRO().SeqRun().fetch(2); //dataset2
        assertEquals("dataset2", seqrun.getName());

        VisualizationGroupI vg = vgmgr.createVisualizationGroup();
        //vgmgr.removeVisualizationGroup(vg);
        vg.close();
        assertEquals(0, vgmgr.getAllVisualizationGroups().size());

        ReplicateGroupI rg1 = vgmgr.createReplicateGroup();
        ReplicateGroupI rg2 = vgmgr.createReplicateGroup();

        ReplicateI rg1r1 = vgmgr.createReplicate(rg1);
        ReplicateI rg2r1 = vgmgr.createReplicate(rg2);

        rg1r1.addSeqRun(seqrun);
        rg2r1.addSeqRun(seqrun);

        vgmgr.registerResolver(new DummyResolver());

        boolean ok = vgmgr.selectAttributeType(AttributeRank.PRIMARY, "NCBI_GENUS");
        assertTrue(ok);
        assertNotNull(vgmgr.getSelectedAttributeType());
        assertNotNull(rg1r1.getSelectedAttributeType());
        assertNotNull(rg2r1.getSelectedAttributeType());

        List<Pair<VisualizationGroupI, DistributionI<Long>>> dists = vgmgr.getDistributions();
        List<Pair<VisualizationGroupI, TreeI<Long>>> trees = vgmgr.getHierarchies();
        assertEquals(2, dists.size());
        assertEquals(2, trees.size());

        //vgmgr.removeReplicateGroup(rg1);
        rg1.close();

        dists = vgmgr.getDistributions();
        trees = vgmgr.getHierarchies();
        assertEquals(1, dists.size());
        assertEquals(1, trees.size());

    }

    @Test
    public void testVGroupRegression() throws MGXException {
        System.out.println("testVGroupRegression");
        VGroupManagerI mgr = VGroupManager.getTestInstance();

        mgr.registerResolver(new ConflictResolver() {
            @Override
            public void resolve(String attrType, List<VisualizationGroupI> vg) {
                System.err.println("Resolving for " + vg.size() + " groups.");
            }
        });
        MGXMasterI master = TestMaster.getRO();
        SeqRunI dataset1 = master.SeqRun().fetch(1);
        assertEquals("dataset1", dataset1.getName());
        VisualizationGroupI vGrp = mgr.createVisualizationGroup();
        vGrp.addSeqRun(dataset1);

        boolean haveException = false;
        try {
            vGrp.selectAttributeType(AttributeRank.PRIMARY, "NCBI_PHYLUM");
        } catch (ConflictingJobsException ex) {
            haveException = true;
        }
        assertTrue(haveException);

        haveException = false;

        try {
            vGrp.selectAttributeType(AttributeRank.PRIMARY, "NCBI_PHYLUM");
        } catch (ConflictingJobsException ex) {
            haveException = true;
        }
        assertTrue(haveException);
    }

    @Test
    public void testRegression() throws MGXException, ConflictingJobsException {
        System.out.println("testRegression");
        VGroupManagerI mgr = VGroupManager.getTestInstance();

        mgr.registerResolver(new ConflictResolver() {
            @Override
            public void resolve(String attrType, List<VisualizationGroupI> vg) {
                System.err.println("Resolving for " + vg.size() + " groups.");
            }
        });
        MGXMasterI master = TestMaster.getRO();
        SeqRunI dataset1 = master.SeqRun().fetch(1);
        assertEquals("dataset1", dataset1.getName());
        VisualizationGroupI vGrp = mgr.createVisualizationGroup();
        //
        vGrp.addSeqRun(dataset1);

//        boolean mayContinue = mgr.selectAttributeType("COG");
//        assertTrue(mayContinue);
        boolean mayContinue = mgr.selectAttributeType("NCBI_PHYLUM");
        assertFalse(mayContinue);

        mayContinue = mgr.selectAttributeType("NCBI_PHYLUM");
        assertFalse(mayContinue);

//        mayContinue = mgr.selectAttributeType("NCBI_PHYLUM");
//        assertFalse(mayContinue);
    }

    private final class DummyResolver implements ConflictResolver {

        @Override
        public void resolve(String attributeType, List<VisualizationGroupI> vgroups) {
            for (VisualizationGroupI vg : vgroups) {
                System.err.println("Resolving group " + vg.getName());
                List<Triple<AttributeRank, SeqRunI, Set<JobI>>> conflicts = vg.getConflicts();
                for (Triple<AttributeRank, SeqRunI, Set<JobI>> t : conflicts) {
                    // always resolve to conflict to first job
                    vg.resolveConflict(AttributeRank.PRIMARY, attributeType, t.getSecond(), t.getThird().toArray(new JobI[]{})[0]);
                }
            }
            vgroups.clear();
        }
    }
}
