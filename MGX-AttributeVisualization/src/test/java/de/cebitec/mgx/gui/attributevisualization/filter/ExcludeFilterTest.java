package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import de.cebitec.mgx.gui.datamodel.misc.Distribution;
import de.cebitec.mgx.gui.datamodel.misc.DistributionFactory;
import de.cebitec.mgx.gui.datamodel.tree.Node;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import de.cebitec.mgx.gui.datamodel.tree.TreeFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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
public class ExcludeFilterTest {

    public ExcludeFilterTest() {
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
    public void testCompareDistAndTree() {
        System.out.println("testCompareDistAndTree");
        MGXMaster m = TestMaster.get2();
        if (m == null) {
            System.err.println("  not tested, private test.");
            return;
        }
        // ncbi_class
        Tree<Long> atb = m.Attribute().getHierarchy(5, 19);
        assertNotNull(atb);
        assertEquals(2274, atb.getNodes().size());

        Distribution atbDist = m.Attribute().getDistribution(5, 19);
        assertNotNull(atbDist);
        assertEquals(86, atbDist.size());
        assertEquals(17447, atbDist.getTotalClassifiedElements());

        AttributeType aType = atbDist.keySet().toArray(new Attribute[0])[0].getAttributeType();
        assertEquals("NCBI_CLASS", aType.getName());

        int nodeCount = 0;
        long count = 0;
        for (Node<Long> node : atb.getNodes()) {
            if (node.getAttribute().getAttributeType().equals(aType)) {
                nodeCount++;
                count += node.getContent().longValue();
            }
        }
        assertEquals(86, nodeCount);
        assertEquals(17447, count);

        Distribution fromTree = DistributionFactory.fromTree(atb, aType);
        assertEquals(86, fromTree.size());
        assertEquals(17447, fromTree.getTotalClassifiedElements());

        for (Attribute attr : fromTree.keySet()) {
            assertEquals("values differ for " + attr.getValue(), fromTree.get(attr), atbDist.get(attr));
            assertTrue(attr.getValue() + " only in tree, not in dist", atbDist.containsKey(attr));
        }
        for (Attribute attr : atbDist.keySet()) {
            assertEquals("values differ for " + attr.getValue(), fromTree.get(attr), atbDist.get(attr));
            assertTrue(attr.getValue() + " only in dist, not in tree", fromTree.containsKey(attr));
        }
    }

    @Test
    public void testMerge() {
        System.out.println("testMerge");
        MGXMaster m = TestMaster.get2();
        if (m == null) {
            System.err.println("  not tested, private test.");
            return;
        }

        Tree<Long> atb = m.Attribute().getHierarchy(5, 19);
        assertNotNull(atb);
        assertEquals(2274, atb.getNodes().size());
        checkNode(atb, "Actinopterygii", 25L);

        Tree<Long> haw = m.Attribute().getHierarchy(5, 20);
        assertNotNull(haw);
        assertEquals(2900, haw.getNodes().size());
        checkNode(haw, "Actinopterygii", 35L);

        // merge trees
        Collection<Tree<Long>> data = new HashSet<>();
        data.add(haw);
        data.add(atb);
        Tree<Long> mergedTree = TreeFactory.mergeTrees(data);
        checkNode(mergedTree, "Actinopterygii", Long.valueOf(60));


        AttributeType ncbiClass = null;
        Attribute attrActinopterygii = null;
        int numClassNodes = 0;
        int numActinopterygii = 0;
        for (Node<Long> node : mergedTree.getNodes()) {
            if (node.getAttribute().getAttributeType().getName().equals("NCBI_CLASS")) {
                ncbiClass = node.getAttribute().getAttributeType();
                numClassNodes++;
                if (node.getAttribute().getValue().equals("Actinopterygii")) {
                    numActinopterygii++;
                    attrActinopterygii = node.getAttribute();
                }
            }
        }
        assertNotNull(ncbiClass);
        assertEquals(100, numClassNodes);
        assertEquals(1, numActinopterygii);

        Distribution atbDist = m.Attribute().getDistribution(5, 19);
        assertNotNull(atbDist);
        assertEquals(86, atbDist.size());
        checkDist(atbDist, "Actinopterygii", 25L);

        Distribution hawDist = m.Attribute().getDistribution(5, 20);
        assertNotNull(hawDist);
        assertEquals(93, hawDist.size());
        checkDist(hawDist, "Actinopterygii", 35L);

        Distribution mergedFromTree = DistributionFactory.fromTree(mergedTree, ncbiClass);
        assertNotNull(mergedFromTree);
        assertEquals(100, mergedFromTree.size());
        Set<String> names = new HashSet<>();
        for (Attribute a : mergedFromTree.keySet()) {
            names.add(a.getValue());
        }
        assertEquals(100, names.size());


        Set<Distribution> set = new HashSet<>();
        set.add(hawDist);
        set.add(atbDist);
        Distribution mergedDists = DistributionFactory.merge(set);
        assertNotNull(mergedDists);
        checkDist(mergedDists, "Actinopterygii", 60L);

        // compare content in both directions
        for (Attribute attr : mergedFromTree.keySet()) {
            assertEquals("values differ for " + attr.getValue(), mergedFromTree.get(attr), mergedDists.get(attr));
            assertTrue(attr.getValue() + " only in tree, not in dist", mergedDists.containsKey(attr));
        }


        assertEquals(100, mergedDists.size());
    }

    @Test
    public void testFilter() {
        System.out.println("filterSingleTree");
        MGXMaster m = TestMaster.get2();
        if (m == null) {
            System.err.println("  not tested, private test.");
            return;
        }
        // ncbi_class
        Tree<Long> atb = m.Attribute().getHierarchy(5, 19);
        assertNotNull(atb);
        assertEquals(2274, atb.getNodes().size());
        Tree<Long> haw = m.Attribute().getHierarchy(5, 20);
        assertNotNull(haw);
        assertEquals(2900, haw.getNodes().size());

        // merge trees
        Collection<Tree<Long>> data = new HashSet<>();
        data.add(haw);
        data.add(atb);
        assertEquals(2, data.size());
        Tree<Long> tree = TreeFactory.mergeTrees(data);
        assertNotNull(tree);
        assertEquals(3092, tree.getNodes().size());

        AttributeType ncbiClass = null;
        for (Node<Long> node : tree.getNodes()) {
            if (node.getAttribute().getAttributeType().getName().equals("NCBI_CLASS")) {
                ncbiClass = node.getAttribute().getAttributeType();
                break;
            }
        }
        assertNotNull(ncbiClass);

        Distribution mergedFromTree = DistributionFactory.fromTree(tree, ncbiClass);
        assertNotNull(mergedFromTree);

        Distribution atbDist = m.Attribute().getDistribution(5, 19);
        assertNotNull(atbDist);
        assertEquals(86, atbDist.size());
        Distribution hawDist = m.Attribute().getDistribution(5, 20);
        assertNotNull(hawDist);
        assertEquals(93, hawDist.size());


        Set<Distribution> set = new HashSet<>();
        set.add(hawDist);
        set.add(atbDist);
        Distribution mergedDists = DistributionFactory.merge(set);
        assertNotNull(mergedDists);

        assertEquals(100, mergedFromTree.size());
        assertEquals(100, mergedDists.size());


        Set<Attribute> filterEntries = new HashSet<>();
        for (Node<Long> node : tree.getNodes()) {
            if (node.getAttribute().getAttributeType().getName().equals("NCBI_CLASS")) {
                if (ncbiClass != null) {
                    assertEquals(ncbiClass, node.getAttribute().getAttributeType());
                }
                ncbiClass = node.getAttribute().getAttributeType();
            }
            if (node.getAttribute().getValue().equals("Bacteria")) {
                filterEntries.add(node.getAttribute());
            }
            if (node.getAttribute().getValue().equals("Eukaryota")) {
                filterEntries.add(node.getAttribute());
            }
        }

        assertEquals(2, filterEntries.size());


        Set<Attribute> blackList = new HashSet<>();
        for (Node<Long> node : tree.getNodes()) {
            if (ncbiClass.equals(node.getAttribute().getAttributeType())) {
                for (Node<Long> pathNode : node.getPath()) {
                    if (filterEntries.contains(pathNode.getAttribute())) {
                        blackList.add(node.getAttribute());
                        break;
                    }
                }
            }
        }
        assertEquals(91, blackList.size());


        Distribution fromTree = DistributionFactory.fromTree(tree, ncbiClass);


        ExcludeFilter filter = new ExcludeFilter(blackList);
        assertEquals(86, atbDist.size());
        filter.filterDist(atbDist);
        assertEquals(8, atbDist.size());

        assertEquals(93, hawDist.size());
        filter.filterDist(hawDist);
        assertEquals(9, hawDist.size());


    }

    private void checkNode(Tree<Long> tree, String name, Long content) {
        assertNotNull(name);
        assertNotNull(content);
        boolean nodeFound = false;
        for (Node<Long> node : tree.getNodes()) {
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

    private void checkDist(Distribution dist, String name, Long content) {
        assertNotNull(name);
        assertNotNull(content);
        boolean found = false;
        for (Attribute attr : dist.keySet()) {
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