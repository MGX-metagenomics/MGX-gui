package de.cebitec.mgx.gui.attributevisualization.filter;

import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author sjaenick
 */
public class ExcludeFilterTest {

    public ExcludeFilterTest() {
    }

//    @Test
//    public void testFilter() {
//        System.out.println("filterSingleTree");
//        MGXMaster m = TestMaster.get2();
//        if (m == null) {
//            System.err.println("  not tested, private test.");
//            return;
//        }
//        // ncbi_class
//        Tree<Long> atb = m.Attribute().getHierarchy(5, 19);
//        assertNotNull(atb);
//        assertEquals(2274, atb.getNodes().size());
//        Tree<Long> haw = m.Attribute().getHierarchy(5, 20);
//        assertNotNull(haw);
//        assertEquals(2900, haw.getNodes().size());
//
//        // merge trees
//        Collection<Tree<Long>> data = new HashSet<>();
//        data.add(haw);
//        data.add(atb);
//        assertEquals(2, data.size());
//        Tree<Long> tree = TreeFactory.mergeTrees(data);
//        assertNotNull(tree);
//        assertEquals(3092, tree.getNodes().size());
//
//        AttributeType ncbiClass = null;
//        for (Node<Long> node : tree.getNodes()) {
//            if (node.getAttribute().getAttributeType().getName().equals("NCBI_CLASS")) {
//                ncbiClass = node.getAttribute().getAttributeType();
//                break;
//            }
//        }
//        assertNotNull(ncbiClass);
//
//        Distribution mergedFromTree = DistributionFactory.fromTree(tree, ncbiClass);
//        assertNotNull(mergedFromTree);
//
//        Distribution atbDist = m.Attribute().getDistribution(5, 19);
//        assertNotNull(atbDist);
//        assertEquals(86, atbDist.size());
//        Distribution hawDist = m.Attribute().getDistribution(5, 20);
//        assertNotNull(hawDist);
//        assertEquals(93, hawDist.size());
//
//
//        Set<Distribution> set = new HashSet<>();
//        set.add(hawDist);
//        set.add(atbDist);
//        Distribution mergedDists = DistributionFactory.merge(set);
//        assertNotNull(mergedDists);
//
//        assertEquals(100, mergedFromTree.size());
//        assertEquals(100, mergedDists.size());
//
//
//        Set<Attribute> filterEntries = new HashSet<>();
//        for (Node<Long> node : tree.getNodes()) {
//            if (node.getAttribute().getAttributeType().getName().equals("NCBI_CLASS")) {
//                if (ncbiClass != null) {
//                    assertEquals(ncbiClass, node.getAttribute().getAttributeType());
//                }
//                ncbiClass = node.getAttribute().getAttributeType();
//            }
//            if (node.getAttribute().getValue().equals("Bacteria")) {
//                filterEntries.add(node.getAttribute());
//            }
//            if (node.getAttribute().getValue().equals("Eukaryota")) {
//                filterEntries.add(node.getAttribute());
//            }
//        }
//
//        assertEquals(2, filterEntries.size());
//
//
//        Set<Attribute> blackList = new HashSet<>();
//        for (Node<Long> node : tree.getNodes()) {
//            if (ncbiClass.equals(node.getAttribute().getAttributeType())) {
//                for (Node<Long> pathNode : node.getPath()) {
//                    if (filterEntries.contains(pathNode.getAttribute())) {
//                        blackList.add(node.getAttribute());
//                        break;
//                    }
//                }
//            }
//        }
//        assertEquals(91, blackList.size());
//
//
//        Distribution fromTree = DistributionFactory.fromTree(tree, ncbiClass);
//
//
//        ExcludeFilter filter = new ExcludeFilter(blackList);
//        assertEquals(86, atbDist.size());
//        filter.filterDist(atbDist);
//        assertEquals(8, atbDist.size());
//
//        assertEquals(93, hawDist.size());
//        filter.filterDist(hawDist);
//        assertEquals(9, hawDist.size());
//
//
//    }

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
            assertEquals(0L, content.longValue(), "Node not found for " + name);
        }
    }

//    private void checkDist(DistributionI<Long> dist, String name, Long content) {
//        assertNotNull(name);
//        assertNotNull(content);
//        boolean found = false;
//        for (AttributeI attr : dist.keySet()) {
//            if (attr.getValue().equals(name)) {
//                found = true;
//                assertEquals(content, dist.get(attr));
//            }
//        }
//        if (!found) {
//            assertEquals(0L, content.longValue());
//        }
//    }
}