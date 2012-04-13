package de.cebitec.mgx.gui.datamodel.tree;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class TreeFactory {

    private static <T> void dumpNode(Node<T> node, int offset) {
        for (int i = 0; i < offset; i++) {
            System.err.print(" ");
        }
        System.err.println(node.getAttribute().toString());
        for (Node<T> n : node.getChildren()) {
            dumpNode(n, offset + 5);
        }
    }

    public static Tree<Long> createTree(Map<Attribute, Long> map) {

        Map<Long, Long> edges = new HashMap<Long, Long>();
        Map<Long, Long> idmap = new HashMap<Long, Long>();

        Tree<Long> tree = new Tree<Long>();

        for (Entry<Attribute, Long> entry : map.entrySet()) {
            Attribute attr = entry.getKey();
            //assert attr != null;
            //assert attr.getId() != null;

            Long count = entry.getValue();

            Node<Long> node = tree.createNode(attr, count);
            //assert node != null;
            idmap.put(attr.getId(), node.getId());

            if (attr.getParentID() == null) {
                //Logger.getLogger("createTree").log(Level.INFO, "found a root node");
                tree.setRoot(node);
            } else {
                // remember the outgoing edge (based on original ids)
                edges.put(attr.getId(), attr.getParentID());
            }
        }

        // fill in the edges with the correct ids
        for (Entry<Long, Long> edge : edges.entrySet()) {
            Long child = idmap.get(edge.getKey());
            Long parent = idmap.get(edge.getValue());
            tree.addEdge(child, parent);
        }

        //Logger.getLogger("createTree").log(Level.INFO, "done");

        tree.build();

        //assert tree.getRoot() != null;

        return tree;
    }

    // used within VisualizationGroup to create a combined tree of all the seqruns
    // contained within a group
    //
    public static Tree<Long> mergeTrees(Collection<Tree<Long>> trees) {
        //Logger.getLogger("mergeTrees").log(Level.INFO, "mergeTrees merging {0} trees", trees.size());
        //assert trees.size() > 0;
        Tree<Long> consensus = new Tree<Long>();

        for (Tree<Long> t : trees) {

            Map<Long, Long> origEdges = new HashMap<Long, Long>();
            Map<Long, Long> idmap = new HashMap<Long, Long>();

            // insert all missing nodes into the consensus tree first
            //
            for (Node<Long> node : t.getNodes()) {
                Node<Long> consNode = consensus.byAttribute(node.getAttribute());
                if (consNode == null) {
                    consNode = consensus.createNode(node.getAttribute(), Long.valueOf(0));

                    if (node.isRoot()) {
                        consensus.setRoot(consNode);
                    } else {
                        origEdges.put(node.getId(), node.getParent().getId());
                    }
                }
                idmap.put(node.getId(), consNode.getId());
            }

            // create edges for added nodes
            //
            for (Entry<Long, Long> edge : origEdges.entrySet()) {
                Long child = idmap.get(edge.getKey());
                Long parent = idmap.get(edge.getValue());
                consensus.addEdge(child, parent);
            }

            // fill in the values
            //
            for (Node<Long> node : t.getNodes()) {
                Node<Long> consNode = consensus.byAttribute(node.getAttribute());
                consNode.setContent(consNode.getContent() + node.getContent());
            }
        }

        consensus.build();

        //assert consensus.getRoot() != null;

        return consensus;
    }

    public static <T> Tree<Map<T, Long>> combineTrees(List<Pair<T, Tree<Long>>> trees) {
        //Logger.getLogger("combineTrees").log(Level.INFO, "combining {0} trees", trees.size());
        Tree<Map<T, Long>> combined = new Tree<Map<T, Long>>();

        for (Pair<T, Tree<Long>> pair : trees) {

            T t = pair.getFirst(); // a vizgroup, typically
            Tree<Long> tree = pair.getSecond();

            Map<Long, Long> origEdges = new HashMap<Long, Long>();
            Map<Long, Long> idmap = new HashMap<Long, Long>();

            // insert all missing nodes into the consensus tree first
            //
            for (Node<Long> node : tree.getNodes()) {
                Node<Map<T, Long>> consNode = combined.byAttribute(node.getAttribute());
                if (consNode == null) {
                    consNode = combined.createNode(node.getAttribute(), new LinkedHashMap<T, Long>());

                    if (node.isRoot()) {
                        combined.setRoot(consNode);
                    } else {
                        origEdges.put(node.getId(), node.getParent().getId());
                    }
                }
                // insert the data
                consNode.getContent().put(t, node.getContent());

                idmap.put(node.getId(), consNode.getId());
            }

            // create edges for added nodes
            //
            for (Entry<Long, Long> edge : origEdges.entrySet()) {
                Long child = idmap.get(edge.getKey());
                Long parent = idmap.get(edge.getValue());
                combined.addEdge(child, parent);
            }

        }

        //assert combined.getRoot() != null;
        
        combined.build();
        
        //dumpNode(combined.getRoot(), 2);

        return combined;
    }
}
