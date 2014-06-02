package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.groups.ConflictingJobsException;
import de.cebitec.mgx.api.groups.VGroupManagerI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.misc.AttributeRank;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.misc.PCAResultI;
import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.misc.Triple;
import de.cebitec.mgx.api.model.JobI;
import de.cebitec.mgx.api.model.SeqRunI;
import de.cebitec.mgx.api.visualization.ConflictResolver;
import de.cebitec.mgx.common.VGroupManager;
import de.cebitec.mgx.gui.util.TestMaster;
import de.cebitec.mgx.newick.NodeI;
import java.util.List;
import java.util.Set;
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

    @Test
    public void testPCA() {
        System.out.println("PCA");
        MGXMaster master = TestMaster.getRO();

        VGroupManagerI vgmgr = VGroupManager.getInstance();
        for (VisualizationGroupI vg : vgmgr.getAllGroups().toArray(new VisualizationGroupI[]{})) {
            vgmgr.removeGroup(vg);
        }
        vgmgr.registerResolver(new Resolver());
        VisualizationGroupI g1 = vgmgr.createGroup();
        g1.setName("grp1");
        g1.addSeqRun(master.SeqRun().fetch(1));

        VisualizationGroupI g2 = vgmgr.createGroup();
        g2.setName("grp2");
        SeqRunI run = master.SeqRun().fetch(2);
        assertNotNull(run);
        g2.addSeqRun(run);

        boolean ret = vgmgr.selectAttributeType(AttributeRank.PRIMARY, "NCBI_CLASS");
        assertTrue(ret);
        List<Pair<VisualizationGroupI, DistributionI>> dists = null;
        try {
            dists = vgmgr.getDistributions();
        } catch (ConflictingJobsException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(dists);
        assertEquals(2, dists.size());
        PCAResultI pca = master.Statistics().PCA(dists, 1, 2);
        assertNotNull(pca);
    }

    @Test
    public void testClustering() {
        System.out.println("Clustering");
        MGXMaster master = TestMaster.getRO();

        VGroupManagerI vgmgr = VGroupManager.getInstance();
        for (VisualizationGroupI vg : vgmgr.getAllGroups().toArray(new VisualizationGroupI[]{})) {
            vgmgr.removeGroup(vg);
        }
        vgmgr.registerResolver(new Resolver());
        VisualizationGroupI g1 = vgmgr.createGroup();
        g1.setName("grp1");
        g1.addSeqRun(master.SeqRun().fetch(1));

        VisualizationGroupI g2 = vgmgr.createGroup();
        g2.setName("grp2");
        g2.addSeqRun(master.SeqRun().fetch(2));

        boolean ret = vgmgr.selectAttributeType(AttributeRank.PRIMARY, "NCBI_CLASS");
        assertTrue(ret);
        List<Pair<VisualizationGroupI, DistributionI>> dists = null;
        try {
            dists = vgmgr.getDistributions();
        } catch (ConflictingJobsException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(dists);
        assertEquals(2, dists.size());

        NodeI root = master.Statistics().Clustering(dists, "euclidean", "ward");
        assertNotNull(root);
        assertEquals(2, root.getChildren().size());
    }

    private class Resolver implements ConflictResolver {

        @Override
        public boolean resolve(List<VisualizationGroupI> vg) {
            for (VisualizationGroupI g : vg.toArray(new VisualizationGroupI[]{})) {
                if (g.getName().equals("grp1")) {
                    for (Triple<AttributeRank, SeqRunI, Set<JobI>> t : g.getConflicts()) {
                        if (t.getSecond().getId() == 1) {
                            g.resolveConflict(t.getFirst(), t.getSecond(), extract(4, t.getThird()));
                        }
                    }
                } else { // grp2
                    for (Triple<AttributeRank, SeqRunI, Set<JobI>> t : g.getConflicts()) {
                        if (t.getSecond().getId() == 2) {
                            g.resolveConflict(t.getFirst(), t.getSecond(), extract(7, t.getThird()));
                        }
                    }
                }
                vg.remove(g);
            }
            return true;
        }

        private JobI extract(long id, Set<JobI> jobs) {
            for (JobI j : jobs) {
                if (j.getId() == id) {
                    return j;
                }
            }
            return null;
        }

    }

}
