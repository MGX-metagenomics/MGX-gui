//package de.cebitec.mgx.gui.controller;
//
//import de.cebitec.mgx.api.MGXMasterI;
//import de.cebitec.mgx.api.exception.MGXException;
//import de.cebitec.mgx.api.groups.ConflictingJobsException;
//import de.cebitec.mgx.api.groups.VGroupManagerI;
//import de.cebitec.mgx.api.groups.VisualizationGroupI;
//import de.cebitec.mgx.api.misc.AttributeRank;
//import de.cebitec.mgx.api.misc.DistributionI;
//import de.cebitec.mgx.api.misc.PrincipalComponent;
//import de.cebitec.mgx.api.misc.PCAResultI;
//import de.cebitec.mgx.api.misc.Pair;
//import de.cebitec.mgx.api.misc.Triple;
//import de.cebitec.mgx.api.model.JobI;
//import de.cebitec.mgx.api.model.SeqRunI;
//import de.cebitec.mgx.api.visualization.ConflictResolver;
//import de.cebitec.mgx.gui.util.TestMaster;
//import de.cebitec.mgx.gui.visgroups.VGroupManager;
//import de.cebitec.mgx.gui.vizfilter.LongToDouble;
//import java.util.List;
//import java.util.Set;
//import static org.junit.Assert.*;
//import org.junit.Test;
//
///**
// *
// * @author sjaenick
// */
//public class StatisticsAccessTest {
//
//    @Test
//    public void testPCA() throws Exception {
//
//        synchronized (this) {
//            System.out.println("PCA");
//            MGXMasterI master = TestMaster.getRO();
//
//            VGroupManagerI vgmgr = VGroupManager.getTestInstance();
//
//            for (VisualizationGroupI vg : vgmgr.getAllVisualizationGroups()) {
//                //vgmgr.removeVisualizationGroup(vg);
//                vg.close();
//            }
//            vgmgr.registerResolver(new Resolver());
//            VisualizationGroupI g1 = vgmgr.createVisualizationGroup();
//            g1.setName("grp1");
//            g1.addSeqRun(master.SeqRun().fetch(1));
//
//            VisualizationGroupI g2 = vgmgr.createVisualizationGroup();
//            g2.setName("grp2");
//            SeqRunI run = master.SeqRun().fetch(2);
//            assertNotNull(run);
//            g2.addSeqRun(run);
//
//            boolean ret = vgmgr.selectAttributeType(AttributeRank.PRIMARY, "NCBI_CLASS");
//            assertTrue(ret);
//            List<Pair<VisualizationGroupI, DistributionI<Long>>> dists = null;
//            try {
//                dists = vgmgr.getDistributions();
//            } catch (ConflictingJobsException ex) {
//                fail(ex.getMessage());
//            }
//            assertNotNull(dists);
//            assertEquals(2, dists.size());
//            List<Pair<VisualizationGroupI, DistributionI<Double>>> filter = new LongToDouble().filter(dists);
//            PCAResultI pca = master.Statistics().PCA(filter, PrincipalComponent.PC1, PrincipalComponent.PC2);
//            assertNotNull(pca);
//        }
//    }
//
//    @Test
//    public void testPCANoPC23() throws Exception {
//        synchronized (this) {
//            System.out.println("PCA no PC2/PC3");
//            MGXMasterI master = TestMaster.getRO();
//
//            VGroupManagerI vgmgr = VGroupManager.getTestInstance();
//
//            for (VisualizationGroupI vg : vgmgr.getAllVisualizationGroups()) {
//                //vgmgr.removeVisualizationGroup(vg);
//                vg.close();
//            }
//            vgmgr.registerResolver(new Resolver());
//            VisualizationGroupI g1 = vgmgr.createVisualizationGroup();
//            g1.setName("grp1");
//            g1.addSeqRun(master.SeqRun().fetch(1));
//
//            VisualizationGroupI g2 = vgmgr.createVisualizationGroup();
//            g2.setName("grp2");
//            SeqRunI run = master.SeqRun().fetch(2);
//            assertNotNull(run);
//            g2.addSeqRun(run);
//
//            assertEquals(2, vgmgr.getActiveVisualizationGroups().size());
//
//            boolean ret = vgmgr.selectAttributeType(AttributeRank.PRIMARY, "EC_number");
//            assertTrue(ret);
//            List<Pair<VisualizationGroupI, DistributionI<Long>>> dists = null;
//            try {
//                dists = vgmgr.getDistributions();
//            } catch (ConflictingJobsException ex) {
//                fail(ex.getMessage());
//            }
//            assertNotNull(dists);
//            assertEquals(2, dists.size());
//            try {
//                List<Pair<VisualizationGroupI, DistributionI<Double>>> filter = new LongToDouble().filter(dists);
//                PCAResultI pca = master.Statistics().PCA(filter, PrincipalComponent.PC2, PrincipalComponent.PC3);
//            } catch (MGXException ex) {
//                if (ex.getMessage().contains("Could not access requested principal components.")) {
//                    return;
//                }
//                fail(ex.getMessage());
//            }
//            fail("Server returned PCA results for invalid PCs.");
//        }
//    }
//
//    @Test
//    public void testClustering() throws Exception {
//        synchronized (this) {
//            System.out.println("Clustering");
//            MGXMasterI master = TestMaster.getRO();
//
//            VGroupManagerI vgmgr = VGroupManager.getTestInstance();
//
//            for (VisualizationGroupI vg : vgmgr.getAllVisualizationGroups()) {
//                //vgmgr.removeVisualizationGroup(vg);
//                vg.close();
//            }
//            vgmgr.registerResolver(new Resolver());
//            VisualizationGroupI g1 = vgmgr.createVisualizationGroup();
//            g1.setName("grp1");
//            g1.addSeqRun(master.SeqRun().fetch(1));
//
//            VisualizationGroupI g2 = vgmgr.createVisualizationGroup();
//            g2.setName("grp2");
//            g2.addSeqRun(master.SeqRun().fetch(2));
//
//            boolean ret = vgmgr.selectAttributeType(AttributeRank.PRIMARY, "NCBI_CLASS");
//            assertTrue(ret);
//            List<Pair<VisualizationGroupI, DistributionI<Long>>> dists = null;
//            try {
//                dists = vgmgr.getDistributions();
//            } catch (ConflictingJobsException ex) {
//                fail(ex.getMessage());
//            }
//            assertNotNull(dists);
//            assertEquals(2, dists.size());
//
//            List<Pair<VisualizationGroupI, DistributionI<Double>>> filter = new LongToDouble().filter(dists);
//
//            String newick = master.Statistics().Clustering(filter, "euclidean", "ward");
//            assertNotNull(newick);
////            assertTrue(newick.contains(g1.getName()));
////            assertTrue(newick.contains(g2.getName()));
//            //assertEquals("(grp1:5.74456264653803,grp2:5.74456264653803);", newick);
//        }
//    }
//
//    private class Resolver implements ConflictResolver {
//
//        @Override
//        public void resolve(String attrType, List<VisualizationGroupI> vg) {
//            for (VisualizationGroupI g : vg.toArray(new VisualizationGroupI[]{})) {
//
//                if (g.getDisplayName().equals("grp1")) {
//                    for (Triple<AttributeRank, SeqRunI, Set<JobI>> t : g.getConflicts()) {
//                        if (t.getSecond().getId() == 1) {
//                            g.resolveConflict(t.getFirst(), attrType, t.getSecond(), extract(4, t.getThird()));
//                        }
//                    }
//                } else { // grp2
//                    for (Triple<AttributeRank, SeqRunI, Set<JobI>> t : g.getConflicts()) {
//                        if (t.getSecond().getId() == 2) {
//                            g.resolveConflict(t.getFirst(), attrType, t.getSecond(), extract(7, t.getThird()));
//                        }
//                    }
//                }
//                vg.remove(g);
//            }
//        }
//
//        private JobI extract(long id, Set<JobI> jobs) {
//            for (JobI j : jobs) {
//                if (j.getId() == id) {
//                    return j;
//                }
//            }
//            return null;
//        }
//
//    }
//
//}
