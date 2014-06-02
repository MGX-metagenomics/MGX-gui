/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.groups;

import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sjaenick
 */
public class VisualizationGroupTest {

    public VisualizationGroupTest() {
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
    public void testSetName() {
        System.out.println("setName");
        VisualizationGroupI vg = VGroupManager.getInstance().createGroup();
        String name = UUID.randomUUID().toString();
        vg.setName(name);
        assertEquals(name, vg.getName());
    }

    @Test
    public void testGetNumSequences() {
        System.out.println("getNumSequences");
        VisualizationGroupI vg = VGroupManager.getInstance().createGroup();
        MGXMaster master = TestMaster.getRO();
        Iterator<SeqRunI> iter = master.SeqRun().fetchall();
        long cnt = 0;
        while (iter.hasNext()) {
            SeqRunI sr = iter.next();
            vg.addSeqRun(sr);
            cnt += sr.getNumSequences();
        }
        assertEquals(cnt, vg.getNumSequences());
    }

    @Test
    public void testGetSeqRuns() {
        System.out.println("getSeqRuns");
        VisualizationGroupI vg = VGroupManager.getInstance().createGroup();
        MGXMaster master = TestMaster.getRO();
        Set<SeqRunI> runs = new HashSet<>();
        Iterator<SeqRunI> iter = master.SeqRun().fetchall();
        while (iter.hasNext()) {
            SeqRunI sr = iter.next();
            vg.addSeqRun(sr);
            runs.add(sr);
        }
        assertEquals(runs, vg.getSeqRuns());
    }

    @Test
    public void testGetAttributeTypes() {
        System.out.println("getAttributeTypes");
        VisualizationGroupI vg = VGroupManager.getInstance().createGroup();
        MGXMaster master = TestMaster.getRO();
        Iterator<SeqRunI> iter = master.SeqRun().fetchall();
        while (iter.hasNext()) {
            vg.addSeqRun(iter.next());
        }
        Iterator<AttributeTypeI> atIter = vg.getAttributeTypes();
        assertNotNull(atIter);
        int cnt = 0;
        while (atIter.hasNext()) {
            AttributeTypeI next = atIter.next();
            System.err.println("  " + next.getName());
            cnt++;
        }
        assertEquals(21, cnt);
    }

    @Test
    public void testaddAndRemoveRuns() {
        System.out.println("addAndRemoveRuns");
        VisualizationGroupI vg = VGroupManager.getInstance().createGroup();
        MGXMaster master = TestMaster.getRO();

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
    public void testGetSelectedAttributeType() {
        System.out.println("getSelectedAttributeType");
        VisualizationGroupI vg = VGroupManager.getInstance().createGroup();
        MGXMaster master = TestMaster.getRO();
        Iterator<SeqRunI> iter = master.SeqRun().fetchall();
        while (iter.hasNext()) {
            vg.addSeqRun(iter.next());
        }
        Iterator<AttributeTypeI> atIter = vg.getAttributeTypes();
        assertNotNull(atIter);

        AttributeTypeI next = null;
        while (atIter.hasNext()) {
            next = atIter.next();
        }
        assertNotNull(next);
        try {
            vg.selectAttributeType(AttributeRank.PRIMARY, next.getName());
        } catch (ConflictingJobsException ex) {
            fail(ex.getMessage());
        }
        assertEquals(next.getName(), vg.getSelectedAttributeType());
    }
//    @Test
//    public void testSelectAttributeType() throws Exception {
//        System.out.println("selectAttributeType");
//        AttributeRank rank = null;
//        String attrType = "";
//        VisualizationGroup instance = null;
//        instance.selectAttributeType(rank, attrType);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    @Test
//    public void testGetConflicts() {
//        System.out.println("getConflicts");
//        VisualizationGroup instance = null;
//        List<Triple<AttributeRank, SeqRun, List<Job>>> expResult = null;
//        List<Triple<AttributeRank, SeqRun, List<Job>>> result = instance.getConflicts();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    @Test
//    public void testResolveConflict() {
//        System.out.println("resolveConflict");
//        AttributeRank rank = null;
//        SeqRun sr = null;
//        Job j = null;
//        VisualizationGroup instance = null;
//        instance.resolveConflict(rank, sr, j);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    @Test
//    public void testAddSeqRun() {
//        System.out.println("addSeqRun");
//        SeqRun sr = null;
//        VisualizationGroup instance = null;
//        instance.addSeqRun(sr);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testRemoveSeqRun() {
//        System.out.println("removeSeqRun");
//        SeqRun sr = null;
//        VisualizationGroup instance = null;
//        instance.removeSeqRun(sr);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetHierarchy() {
//        System.out.println("getHierarchy");
//        VisualizationGroup instance = null;
//        Tree<Long> expResult = null;
//        Tree<Long> result = instance.getHierarchy();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    @Test
//    public void testGetDistribution() throws Exception {
//        System.out.println("getDistribution");
//        VisualizationGroup instance = null;
//        Distribution expResult = null;
//        Distribution result = instance.getDistribution();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//
//    @Test
//    public void testGetSaveSet() {
//        System.out.println("getSaveSet");
//        List<String> requestedAttrs = null;
//        VisualizationGroup instance = null;
//        Map<SeqRun, Set<Attribute>> expResult = null;
//        Map<SeqRun, Set<Attribute>> result = instance.getSaveSet(requestedAttrs);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
