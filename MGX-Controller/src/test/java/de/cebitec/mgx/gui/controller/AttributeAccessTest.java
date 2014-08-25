package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.DistributionI;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.common.DistributionFactory;
import de.cebitec.mgx.common.TreeFactory;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.util.TestMaster;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author sj
 */
public class AttributeAccessTest {

    public AttributeAccessTest() {
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
    public void testGetDistribution() throws MGXException {
        System.out.println("getDistribution");
        DistributionI dist = TestMaster.getRO().Attribute().getDistribution(6, 3);
        assertNotNull(dist);
        assertEquals(5, dist.size());
        assertEquals(24, dist.getTotalClassifiedElements());
    }

    @Test
    public void testFetch() throws MGXException {
        System.out.println("fetch");
        AttributeI attr = TestMaster.getRO().Attribute().fetch(1);
        assertNotNull(attr);
        assertNotNull(attr.getAttributeType());
        assertEquals("50.8", attr.getValue());
        assertEquals("GC", attr.getAttributeType().getName());
    }

    @Test
    public void testGetHierarchy() throws MGXException {
        System.out.println("getHierarchy");
        TreeI<Long> tree = TestMaster.getRO().Attribute().getHierarchy(6, 3);
        assertNotNull(tree);
        assertEquals(30, tree.getNodes().size());
        assertNotNull(tree.getRoot());

        NodeI<Long> n = null;
        for (NodeI<Long> x : tree.getNodes()) {
            if (x.getAttribute().getValue().equals("Bacteroidetes")) {
                n = x;
                break;
            }
        }
        assertNotNull(n);
        assertEquals(2, n.getDepth());

        // check the path
        NodeI<Long>[] path = n.getPath();
        assertEquals(3, path.length);
        assertEquals(path[0], tree.getRoot());
        assertNotNull(path[1]);
        assertEquals(path[2], n);
    }

    @Test
    public void testFilterTree() throws MGXException {
        System.out.println("filterTree");
        TreeI<Long> tree = TestMaster.getRO().Attribute().getHierarchy(6, 3);
        assertNotNull(tree);
        assertEquals(30, tree.getNodes().size());
        assertNotNull(tree.getRoot());

        NodeI<Long> n = null;
        for (NodeI x : tree.getNodes()) {
            if (x.getAttribute().getValue().equals("Bacteroidetes")) {
                n = x;
                break;
            }
        }
        assertNotNull(n);
        assertEquals(2, n.getDepth());

        Set<AttributeI> exclude = new HashSet<>();
        exclude.add(n.getAttribute());

        tree = TreeFactory.filter(tree, exclude);
        assertNotNull(tree);
        assertEquals(25, tree.getNodes().size());
        assertNotNull(tree.getRoot());
    }

    @Test
    public void testCloneTree() throws MGXException {
        System.out.println("cloneTree");
        TreeI<Long> tree = TestMaster.getRO().Attribute().getHierarchy(6, 3);
        assertNotNull(tree);
        assertEquals(30, tree.getNodes().size());
        assertNotNull(tree.getRoot());
        
        TreeI<Long> tree2 = TreeFactory.clone(tree);
        assertNotNull(tree2);
        assertNotSame(tree2, tree);
        assertEquals(tree.getNodes().size(), tree2.getNodes().size());
    }

//    @Test
//    public void testMergeD() {
//        System.out.println("testMergeD");
//        MGXMaster m = TestMaster.get2();
//        if (m == null) {
//            System.err.println("  not tested, private test.");
//            return;
//        }
//
//        Distribution atbDist = m.Attribute().getDistribution(5, 19);
//        assertNotNull(atbDist);
//        assertEquals(86, atbDist.size());
//        assertEquals(17447, atbDist.getTotalClassifiedElements());
//        long total = 0;
//        for (Attribute attr : atbDist.keySet()) {
//            total += atbDist.get(attr).longValue();
//        }
//        assertEquals(17447, total);
//        checkDist(atbDist, "Actinopterygii", 25L);
//        Attribute a1 = findDist(atbDist, "Actinopterygii");
//        assertNotNull(a1);
//
//        Distribution hawDist = m.Attribute().getDistribution(5, 20);
//        assertNotNull(hawDist);
//        assertEquals(93, hawDist.size());
//        assertEquals(46406, hawDist.getTotalClassifiedElements());
//        total = 0;
//        for (Attribute attr : hawDist.keySet()) {
//            total += hawDist.get(attr).longValue();
//        }
//        assertEquals(46406, total);
//        checkDist(hawDist, "Actinopterygii", 35L);
//        Attribute a2 = findDist(hawDist, "Actinopterygii");
//        assertNotNull(a2);
//
//        assertEquals(a1.getAttributeType(), a2.getAttributeType());
//        assertEquals(a1.getValue(), a2.getValue());
//        assertEquals(a1.getMaster(), a2.getMaster());
//        assertEquals(a1, a2);
//
//        Set<Distribution> set = new HashSet<>();
//        set.add(hawDist);
//        set.add(atbDist);
//        Distribution mergedDists = DistributionFactory.merge(set);
//        assertNotNull(mergedDists);
//        assertEquals(17447 + 46406, mergedDists.getTotalClassifiedElements());
//
//        int numfound = 0;
//        Attribute actino = null;
//        for (Attribute attr : mergedDists.keySet()) {
//            if (attr.getValue().equals("Actinopterygii")) {
//                numfound++;
//                //assertNull(actino);
//                actino = attr;
//            }
//        }
//        assertEquals(1, numfound);
//
//
//        checkDist(mergedDists, "Actinopterygii", 60L);
//
////        // compare content in both directions
////        for (Attribute attr : mergedFromTree.keySet()) {
////            assertEquals("values differ for " + attr.getValue(), mergedFromTree.get(attr), mergedDists.get(attr));
////            assertTrue(attr.getValue() + " only in tree, not in dist", mergedDists.containsKey(attr));
////        }
//
//
//        assertEquals(100, mergedDists.size());
//    }

//    @Test
//    public void testMergeH() {
//        System.out.println("testMergeH");
//        MGXMaster m = TestMaster.get2();
//        if (m == null) {
//            System.err.println("  not tested, private test.");
//            return;
//        }
//
//        Tree<Long> atb = m.Attribute().getHierarchy(5, 19);
//        assertNotNull(atb);
//        assertEquals(2274, atb.getNodes().size());
//        checkNode(atb, "Actinopterygii", 25L);
//        Node<Long> n1 = findNode(atb, "Actinopterygii");
//
//        Tree<Long> haw = m.Attribute().getHierarchy(5, 20);
//        assertNotNull(haw);
//        assertEquals(2900, haw.getNodes().size());
//        checkNode(haw, "Actinopterygii", 35L);
//        Node<Long> n2 = findNode(haw, "Actinopterygii");
//
//        assertTrue(TreeFactory.nodesAreEqual(n1, n2));
//
//        // merge trees
//        Collection<Tree<Long>> data = new HashSet<>();
//        data.add(haw);
//        data.add(atb);
//        Tree<Long> mergedTree = TreeFactory.mergeTrees(data);
//
//        int numfound = 0;
//        Node<Long> actino = null;
//        for (Node<Long> node : mergedTree.getNodes()) {
//            if (node.getAttribute().getValue().equals("Actinopterygii")) {
//                numfound++;
//                assertNull(actino);
//                actino = node;
//            }
//        }
//        assertEquals(1, numfound);
//        assertEquals("Chordata", actino.getParent().getAttribute().getValue());
//
//        checkNode(mergedTree, "Actinopterygii", Long.valueOf(60));
//    }

    @Test
    public void verifyTreeStructure() throws MGXException {
        System.out.println("verifyTreeStructure");
        TreeI<Long> tree = TestMaster.getRO().Attribute().getHierarchy(6, 3);
        assertNotNull(tree);

        for (NodeI<Long> node : tree.getNodes()) {
            if (!node.isRoot()) {
                assertNotNull(node.getParent());
            }

            if (node.isLeaf()) {
                assertFalse(node.hasChildren());
            }
        }

        // nodes and node parents attributes types have to differ
        for (NodeI<Long> node : tree.getNodes()) {
            if (!node.isRoot()) {
                assertNotSame(node.getAttribute().getAttributeType(), node.getParent().getAttribute().getAttributeType());
                assertFalse(node.getAttribute().getAttributeType().getName().equals(node.getParent().getAttribute().getAttributeType().getName()));
            }
        }

        for (NodeI<Long> node : tree.getNodes()) {
            assertEquals(node.getDepth() + 1, node.getPath().length);
        }
    }

    @Test
    public void testDistFromTree() throws MGXException {
        System.out.println("distFromTree");
        DistributionI dist = TestMaster.getRO().Attribute().getDistribution(6, 3);
        assertNotNull(dist);
        assertEquals(5, dist.size());
        assertEquals(24, dist.getTotalClassifiedElements());

        AttributeTypeI aType = dist.keySet().toArray(new Attribute[]{})[0].getAttributeType();
        assertNotNull(aType);
        assertEquals("Bergey_class", aType.getName());

        TreeI<Long> tree = TestMaster.getRO().Attribute().getHierarchy(6, 3);
        assertNotNull(tree);

        // count manually
        int i = 0;
        for (NodeI<Long> node : tree.getNodes()) {
            if (node.getAttribute().getAttributeType().equals(aType)) {
                i++;
            }
        }
        assertEquals(5, i);

        DistributionI fromTree = DistributionFactory.fromTree(tree, aType);
        assertNotNull(fromTree);
        assertEquals(5, fromTree.size());
        assertEquals(24, fromTree.getTotalClassifiedElements());
    }

    @Test
    public void testMergeDist() throws MGXException {
        System.out.println("mergeDistributions");
        DistributionI dist = TestMaster.getRO().Attribute().getDistribution(6, 3);
        assertNotNull(dist);
        assertEquals(5, dist.size());
        assertEquals(24, dist.getTotalClassifiedElements());
        List<DistributionI> twoTimes = new ArrayList<>();
        twoTimes.add(dist);
        twoTimes.add(dist);

        DistributionI merged = DistributionFactory.merge(twoTimes);
        assertNotNull(merged);
        assertEquals(5, merged.size());
        assertEquals(48, merged.getTotalClassifiedElements());

        // bergey_order
        DistributionI dist2 = TestMaster.getRO().Attribute().getDistribution(7, 3);
        assertNotNull(dist2);
        assertEquals(4, dist2.size());
        assertEquals(21, dist2.getTotalClassifiedElements());
        List<DistributionI> twoDists = new ArrayList<>();
        twoDists.add(dist);
        twoDists.add(dist2);
        DistributionI twoDifferent = DistributionFactory.merge(twoDists);
        assertNotNull(twoDifferent);
        assertEquals(9, twoDifferent.size());
        assertEquals(45, twoDifferent.getTotalClassifiedElements());
    }

    private void checkNode(TreeI<Long> tree, String name, Long content) {
        assertNotNull(name);
        assertNotNull(content);
        boolean nodeFound = false;
        for (NodeI<Long> node : tree.getNodes()) {
            if (node.getAttribute().getValue().equals(name)) {
                assertFalse(nodeFound);
                nodeFound = true;
                assertEquals(content, node.getContent());
            }
        }
        if (!nodeFound) {
            assertEquals("Node not found for " + name, 0L, content.longValue());
        }
    }

    private static <T> NodeI<T> findNode(TreeI<T> tree, String name) {
        assertNotNull(name);
        for (NodeI<T> node : tree.getNodes()) {
            if (node.getAttribute().getValue().equals(name)) {
                return node;
            }
        }
        assert false;
        return null;
    }

    private static AttributeI findDist(DistributionI d, String name) {
        assertNotNull(name);
        for (AttributeI a : d.keySet()) {
            if (a.getValue().equals(name)) {
                return a;
            }
        }
        assert false;
        return null;
    }

    private void checkDist(DistributionI dist, String name, Long content) {
        assertNotNull(name);
        assertNotNull(content);
        boolean found = false;
        for (AttributeI attr : dist.keySet()) {
            if (attr.getValue().equals(name)) {
                found = true;
                assertEquals(content, dist.get(attr));
            }
        }
        if (!found) {
            assertEquals(0L, content.longValue());
        }
    }
}