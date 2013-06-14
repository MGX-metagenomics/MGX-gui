package de.cebitec.mgx.gui.datamodel.tree;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Identifiable;
import de.cebitec.mgx.gui.datamodel.misc.Pair;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author sjaenick
 */
public class TreeFactory {

    private static <T> void dumpNode(Node<? extends T> node, int offset) {
        for (int i = 0; i < offset; i++) {
            System.err.print(" ");
        }
        System.err.println(node.getAttribute().toString());
        for (Node<? extends T> n : node.getChildren()) {
            dumpNode(n, offset + 5);
        }
    }

    public static <T> Tree<T> createTree(final Map<Attribute, T> map) {

        Checker.sanityCheck(map.keySet());

        Map<Long, Node<T>> idmap = new HashMap<>(map.size()); // attr id to node

        Tree<T> tree = new Tree<>();

        for (Entry<Attribute, ? extends T> entry : map.entrySet()) {
            Attribute attr = entry.getKey();
            Node<T> node = tree.createNode(attr, entry.getValue());
            idmap.put(attr.getId(), node);
        }
        assert map.keySet().size() == idmap.size();
        assert tree.nodes.size() == map.size();

        for (Attribute attr : map.keySet()) {
            if (attr.getParentID() == Identifiable.INVALID_IDENTIFIER) {
                tree.setRoot(idmap.get(attr.getId()));
            } else {
                Node<T> child = idmap.get(attr.getId());
                Node<T> parent = idmap.get(attr.getParentID());
                assert child != null;
                assert parent != null;
                tree.addEdge(child, parent);
            }
        }

        tree.build();

        Checker.checkTree(tree);
        return tree;
    }
    
    
    public static Tree<Long> mergeTrees2(Collection<Tree<Long>> trees) {
        Tree<Long> consensus = new Tree<>();
        for (Tree<Long> t : trees) {
            Node<Long> root = t.getRoot();
            Node<Long> consRoot = null;
            if (consensus.getRoot() == null) {
                consRoot = consensus.createNode(root.getAttribute(), 0L);
                consensus.setRoot(consRoot);
            }
            for (Node<Long> child : root.getChildren()) {
                // FIXME
            }
        }
        return consensus;
    }

// used within VisualizationGroup to create a combined tree of all the seqruns
// contained within a group
//
    public static Tree<Long> mergeTrees(Collection<Tree<Long>> trees) {
        //Logger.getLogger("mergeTrees").log(Level.INFO, "mergeTrees merging {0} trees", trees.size());
        //assert trees.size() > 0;
        Tree<Long> consensus = new Tree<>();

        for (Tree<Long> t : trees) {

            Checker.checkTree(t);

            Map<Node<Long>, Node<Long>> origEdges = new HashMap<>();
            Map<Long, Long> idmap = new HashMap<>();

            // insert all missing nodes into the consensus tree first
            //
            for (Node<Long> node : t.getNodes()) {
                Node<Long> consNode = null;
                for (Node<Long> n : consensus.getNodes()) {
                    if (nodesAreEqual(node, n)) {
                        consNode = n;
                    }
                }
                if (consNode == null) {
                    consNode = consensus.createNode(node.getAttribute(), 0L);

                    if (node.isRoot()) {
                        consensus.setRoot(consNode);
                    } else {
                        origEdges.put(node, node.getParent());
                    }
                }
                idmap.put(node.getId(), consNode.getId());
            }

            assert t.getNodes().size() == idmap.size();

            // create edges for added nodes
            //
            for (Entry<Node<Long>, Node<Long>> edge : origEdges.entrySet()) {
                consensus.addEdge(edge.getKey(), edge.getValue());
            }


        }

        consensus.build();

        for (Tree<Long> t : trees) {
            // fill in the values
            //
            for (Node<Long> node : t.getNodes()) {
                Node<Long> consNode = null;
                for (Node<Long> n : consensus.getNodes()) {
                    if (nodesAreEqual(node, n)) {
                        consNode = n;
                    }
                }
                assert consNode != null;
                long sum = consNode.getContent().longValue() + node.getContent().longValue();
                consNode.setContent(Long.valueOf(sum));
            }
        }


        Checker.checkTree(consensus);
        return consensus;
    }

    public static <T> Tree<Map<T, Long>> combineTrees(List<Pair<T, Tree<Long>>> trees) {
        Tree<Map<T, Long>> combined = new Tree<>();

        for (Pair<T, Tree<Long>> pair : trees) {
            Tree<Long> tree = pair.getSecond();
            Checker.checkTree(tree);
        }

        for (Pair<T, Tree<Long>> pair : trees) {

            T t = pair.getFirst(); // a vizgroup, typically
            Tree<Long> tree = pair.getSecond();

            Map<Node<Long>, Node<Long>> origEdges = new HashMap<>();
            Map<Node<Long>, Node<Map<T, Long>>> idmap = new HashMap<>();

            // insert all missing nodes into the consensus tree first
            //
            for (Node<Long> node : tree.getNodes()) {
                Node<Map<T, Long>> consNode = null;
                for (Node<Map<T, Long>> n : combined.getNodes()) {
                    if (nodesAreEqual(node, n)) {
                        consNode = n;
                    }
                }
                if (consNode == null) {
                    consNode = combined.createNode(node.getAttribute(), new LinkedHashMap<T, Long>());

                    if (node.isRoot()) {
                        combined.setRoot(consNode);
                    } else {
                        origEdges.put(node, node.getParent());
                    }
                }
                // insert the data
                consNode.getContent().put(t, node.getContent());

                idmap.put(node, consNode);
            }

            // create edges for added nodes
            //
            for (Entry<Node<Long>, Node<Long>> edge : origEdges.entrySet()) {
                Node<Map<T, Long>> child = idmap.get(edge.getKey());
                Node<Map<T, Long>> parent = idmap.get(edge.getValue());
                combined.addEdge(child, parent);
            }

        }

        combined.build();
        Checker.checkTree(combined);
        return combined;
    }

    private static boolean nodesAreEqual(Node n1, Node n2) {
        // compare depth
        if (n1.isRoot() == n2.isRoot() && n1.getDepth() == n2.getDepth()) {
            // compare attribute value
            if (n1.getAttribute().getValue().equals(n2.getAttribute().getValue())) {
                // compare attribute type name
                if (n1.getAttribute().getAttributeType().getName().equals(n2.getAttribute().getAttributeType().getName())) {
                    if (n1.isRoot() && n2.isRoot()) {
                        return true;
                    }
                    return nodesAreEqual(n1.getParent(), n2.getParent());
                }
            }
        }
        return false;
    }
}
