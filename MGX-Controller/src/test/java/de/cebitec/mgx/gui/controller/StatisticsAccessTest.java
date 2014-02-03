package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.misc.AttributeRank;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.misc.Triple;
import de.cebitec.mgx.gui.groups.ConflictingJobsException;
import de.cebitec.mgx.gui.groups.VGroupManager;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
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
public class StatisticsAccessTest {

    public StatisticsAccessTest() {
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
//    public void testClustering2() {
//        for (int i = 0; i < 35; i++) {
//            testClustering();
//        }
//    }

    @Test
    public void testClustering() {
        System.out.println("Clustering");
        MGXMaster master = TestMaster.getRO();

        VGroupManager vgmgr = VGroupManager.getInstance();
        for (VisualizationGroup vg : vgmgr.getAllGroups().toArray(new VisualizationGroup[]{})) {
            vgmgr.removeGroup(vg);
        }
        vgmgr.registerResolver(new Resolver());
        VisualizationGroup g1 = vgmgr.createGroup();
        g1.setName("grp1");
        g1.addSeqRun(master.SeqRun().fetch(1));

        VisualizationGroup g2 = vgmgr.createGroup();
        g2.setName("grp2");
        g2.addSeqRun(master.SeqRun().fetch(2));

        boolean ret = vgmgr.selectAttributeType(AttributeRank.PRIMARY, "NCBI_CLASS");
        assertTrue(ret);
        List<Pair<VisualizationGroup, Distribution>> dists = null;
        try {
            dists = vgmgr.getDistributions();
        } catch (ConflictingJobsException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(dists);
        assertEquals(2, dists.size());

        String newick = master.Statistics().Clustering(dists);
        assertNotNull(newick);
        assertTrue(newick.equals("(grp1:5.74456264653803,grp2:5.74456264653803);") || newick.equals("(grp2:5.74456264653803,grp1:5.74456264653803);"));
    }

    private class Resolver implements VGroupManager.ConflictResolver {

        @Override
        public boolean resolve(List<VisualizationGroup> vg) {
            for (VisualizationGroup g : vg.toArray(new VisualizationGroup[]{})) {
                if (g.getName().equals("grp1")) {
                    for (Triple<AttributeRank, SeqRun, List<Job>> t : g.getConflicts()) {
                        if (t.getSecond().getId() == 1) {
                            g.resolveConflict(t.getFirst(), t.getSecond(), extract(4, t.getThird()));
                        }
                    }
                } else { // grp2
                    for (Triple<AttributeRank, SeqRun, List<Job>> t : g.getConflicts()) {
                        if (t.getSecond().getId() == 2) {
                            g.resolveConflict(t.getFirst(), t.getSecond(), extract(7, t.getThird()));
                        }
                    }
                }
                vg.remove(g);
            }
            return true;
        }

        private Job extract(long id, List<Job> jobs) {
            for (Job j : jobs) {
                if (j.getId() == id) {
                    return j;
                }
            }
            return null;
        }

    }

}
