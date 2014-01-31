package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.gui.datamodel.Job;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import de.cebitec.mgx.gui.datamodel.misc.AttributeRank;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import de.cebitec.mgx.gui.datamodel.misc.Point;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.datamodel.misc.Triple;
import de.cebitec.mgx.gui.groups.ConflictingJobsException;
import de.cebitec.mgx.gui.groups.VGroupManager;
import de.cebitec.mgx.gui.groups.VisualizationGroup;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

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
    public void testClustering() {
        System.out.println("Clustering");
        MGXMaster master = TestMaster.getRO();

//        Distribution dist = master.Attribute().getDistribution(6, 3);
//        assertNotNull(dist);
//        assertEquals(5, dist.size());
        VGroupManager vgmgr = VGroupManager.getInstance();
        vgmgr.registerResolver(new Resolver(master));
        VisualizationGroup g1 = vgmgr.createGroup();
        g1.setName("grp1");
        g1.addSeqRun(master.SeqRun().fetch(1));
        g1.addSeqRun(master.SeqRun().fetch(3));

        VisualizationGroup g2 = vgmgr.createGroup();
        g2.setName("grp2");
        g2.addSeqRun(master.SeqRun().fetch(2));

        vgmgr.selectAttributeType(AttributeRank.PRIMARY, "NCBI_CLASS");
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
        assertEquals("FIXME", newick);

    }

    private class Resolver implements VGroupManager.ConflictResolver {

        private final MGXMaster master;

        public Resolver(MGXMaster master) {
            this.master = master;
        }

        @Override
        public boolean resolve(List<VisualizationGroup> vg) {
            for (VisualizationGroup g : vg.toArray(new VisualizationGroup[]{})) {
                if (g.getName().equals("grp1")) {
                    for (Triple<AttributeRank, SeqRun, List<Job>> t : g.getConflicts()) {
                        if (t.getSecond().getId() == 1) {
                            g.resolveConflict(t.getFirst(), t.getSecond(), t.getThird().get(0));

                        }
                    }
                } else { // grp2
                    for (Triple<AttributeRank, SeqRun, List<Job>> t : g.getConflicts()) {
                        if (t.getSecond().getId() == 2) {
                            g.resolveConflict(t.getFirst(), t.getSecond(), t.getThird().get(0));

                        }
                    }
                }
                vg.remove(g);
            }
            return true;
        }

    }

}
