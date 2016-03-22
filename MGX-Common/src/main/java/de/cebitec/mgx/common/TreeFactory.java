package de.cebitec.mgx.common;

import de.cebitec.mgx.api.misc.Pair;
import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.AttributeTypeI;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import de.cebitec.mgx.gui.datamodel.tree.Tree;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author sjaenick
 */
public class TreeFactory {

    public static <T> void dumpNode(NodeI<? extends T> node, int offset) {
        for (int i = 0; i < offset; i++) {
            System.err.print("TreeFactory#dump:  ");
        }
        System.err.println("TreeFactory#dump: "+node.getAttribute().toString());
        if (node.hasChildren()) {
            for (NodeI<? extends T> n : node.getChildren()) {
                dumpNode(n, offset + 5);
            }
        }
    }

    public static <T> TreeI<T> createTree(final Map<AttributeI, T> map) {

        //Checker.sanityCheck(map.keySet());
        Map<Long, NodeI<T>> idmap = new HashMap<>(map.size()); // attr id to node
        Map<AttributeI, T> disconnected = new HashMap<>();

        TreeI<T> tree = new Tree<>();
        for (Entry<AttributeI, T> entry : map.entrySet()) {
            AttributeI attr = entry.getKey();
            if (attr.getParentID() == Identifiable.INVALID_IDENTIFIER) {
                NodeI<T> root = tree.createRootNode(attr, entry.getValue());
                idmap.put(attr.getId(), root);
            } else if (idmap.containsKey(attr.getParentID())) {
                NodeI<T> parent = idmap.get(attr.getParentID());
                NodeI<T> child = parent.addChild(attr, entry.getValue());
                idmap.put(child.getAttribute().getId(), child);
            } else {
                disconnected.put(attr, entry.getValue());
            }
        }

        while (!disconnected.isEmpty()) {
            Iterator<Entry<AttributeI, T>> it = disconnected.entrySet().iterator();
            while (it.hasNext()) {
                Entry<AttributeI, T> next = it.next();
                AttributeI attr = next.getKey();
                if (idmap.containsKey(attr.getParentID())) {
                    NodeI<T> parent = idmap.get(attr.getParentID());
                    NodeI<T> child = parent.addChild(attr, disconnected.get(attr));
                    it.remove();
                    idmap.put(attr.getId(), child);
                }
            }
        }

        assert map.keySet().size() == idmap.size();
        assert tree.size() == map.size();

        Checker.checkTree(tree);
        return tree;
    }

    public static TreeI<Long> mergeTrees(Collection<Future<TreeI<Long>>> trees) throws InterruptedException, ExecutionException {
        TreeI<Long> consensus = new Tree<>();
        DataMerger<Long, Long> adder = new Adder();

        for (Future<TreeI<Long>> f : trees) {
            TreeI<Long> t = f.get();
            NodeI<Long> root = consensus.getRoot();
            if (root == null) {
                root = consensus.createRootNode(t.getRoot().getAttribute(), t.getRoot().getContent());
            } else {
                root.setContent(adder.merge(root.getContent(), t.getRoot().getContent()));
            }
            ContentAccessor<Long, Long> cac = new NodeAccess<>();
            addChildren(root, t.getRoot().getChildren(), cac, adder);
        }
        return consensus;
    }

    public static TreeI<Long> createKRONATree(TreeI<Long> tree) {

        tree = TreeFactory.clone(tree);

        // for KRONA plots, we need each nodes count to be the number
        // of reads most specifically assigned to this node only, excluding
        // reads assigned to a more specific entry.
        // Thus, we iterate over all nodes and subtract the sum of reads assigned
        // to the immediate child nodes.
        Map<AttributeI, Long> newContent = new HashMap<>(tree.getNodes().size());
        for (NodeI<Long> node : tree.getNodes()) {
            Long numPathsEndingHere = node.getContent();
            if (!node.isLeaf()) {
                numPathsEndingHere = numPathsEndingHere - nodeSum(node.getChildren());
            }
            newContent.put(node.getAttribute(), numPathsEndingHere);
        }

        // ..and update
        for (NodeI<Long> node : tree.getNodes()) {
            node.setContent(newContent.get(node.getAttribute()));
        }
        return tree;
    }

    public static TreeI<Long> filter(TreeI<Long> tree, Set<AttributeI> exclude) {
        TreeI<Long> clone = new Tree<>();

        // clone root node
        if (!exclude.contains(tree.getRoot().getAttribute())) {
            long rootContent = tree.getRoot().getContent();
            NodeI<Long> cloneRoot = clone.createRootNode(tree.getRoot().getAttribute(), rootContent);

            // filter children recursively
            filterChildren(cloneRoot, tree.getRoot().getChildren(), new LongCloner(), exclude);
        }

        return clone;
    }

    private static <T> void filterChildren(NodeI<T> parent, Set<NodeI<T>> children, ContentCloner<T> cloner, Set<AttributeI> exclude) {
        for (NodeI<T> child : children) {
            if (!exclude.contains(child.getAttribute())) {
                NodeI<T> clonedChild = parent.addChild(child.getAttribute(), cloner.cloneContent(child.getContent()));
                if (!child.isLeaf()) {
                    filterChildren(clonedChild, child.getChildren(), cloner, exclude);
                }
            }
        }
    }

    public static TreeI<Long> clone(TreeI<Long> tree) {
        TreeI<Long> clone = new Tree<>();

        // clone root node
        long rootContent = tree.getRoot().getContent();
        NodeI<Long> cloneRoot = clone.createRootNode(tree.getRoot().getAttribute(), rootContent);

        // clone children recursively
        cloneChildren(cloneRoot, tree.getRoot().getChildren(), new LongCloner());

        return clone;
    }

    private static <T> void cloneChildren(NodeI<T> parent, Set<NodeI<T>> children, ContentCloner<T> cloner) {
        for (NodeI<T> child : children) {
            NodeI<T> clonedChild = parent.addChild(child.getAttribute(), cloner.cloneContent(child.getContent()));
            if (!child.isLeaf()) {
                cloneChildren(clonedChild, child.getChildren(), cloner);
            }
        }
    }

    private static <T, U, V> void addChildren(NodeI<T> parent, Set<NodeI<U>> children, ContentAccessor<V, U> cac, DataMerger<T, V> merger) {
        for (NodeI<U> node : children) {
            NodeI<T> correctChild = null;

            if (parent.hasChildren()) {
                for (NodeI<T> candidate : parent.getChildren()) {
                    if (nodesAreEqual(node, candidate)) {
                        correctChild = candidate;
                        V content = cac.getContent(node);
                        T mergedContent = merger.merge(correctChild.getContent(), content);
                        correctChild.setContent(mergedContent);
                        break;
                    }
                }
            }

            if (correctChild == null) {
                T merged = merger.merge(merger.getDefault(), cac.getContent(node));
                correctChild = parent.addChild(node.getAttribute(), merged);
            }

            assert correctChild != null;

            if (!node.isLeaf()) {
                addChildren(correctChild, node.getChildren(), cac, merger);
            }
        }
    }

    public static <U, V> TreeI<Map<U, V>> combineTrees(List<Pair<U, TreeI<V>>> trees) {

        TreeI<Map<U, V>> combined = new Tree<>();
        DataMerger<Map<U, V>, Pair<U, V>> merger = new Combiner<>();
        PairNodeAccess<U, V> pna = new PairNodeAccess<>();
        ContentAccessor<Pair<U, V>, V> cac = pna;

        for (Pair<U, TreeI<V>> pair : trees) {
            pna.setFirst(pair.getFirst());
            TreeI<V> t = pair.getSecond();
            NodeI<Map<U, V>> root = combined.getRoot();
            if (root == null) {
                Pair<U, V> newData = cac.getContent(t.getRoot());
                Map<U, V> content = merger.merge(merger.getDefault(), newData);
                root = combined.createRootNode(t.getRoot().getAttribute(), content);
            } else {
                Pair<U, V> data = cac.getContent(t.getRoot());
                Map<U, V> content = merger.merge(root.getContent(), data);
                root.setContent(content);
            }
            addChildren(root, t.getRoot().getChildren(), cac, merger);
        }

        Checker.checkTree(combined);
        return combined;
    }

//    private static <T, U, V extends Pair<T, U>> V getPair(T first, U second) {
//        return (V) new Pair<>(first, second);
//    }
    public static <T, U> boolean nodesAreEqual(NodeI<T> n1, NodeI<U> n2) {
        if (n1.isRoot() && n2.isRoot()) {
            return true;
        }
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

    private static long nodeSum(Set<NodeI<Long>> nodes) {
        int sum = 0;
        for (NodeI<Long> n : nodes) {
            sum += n.getContent();
        }
        return sum;
    }

    public static <T> AttributeTypeI[] getLongestPath(TreeI<T> tree) {
        /*
         * actually, this is still wrong. we don't need the longest path, but
         * the most complete one. the current code will fail when several paths
         * have equal length, but different content, e.g.
         * 
         *  phylum -- subphylum -- order -- foo
         *  phylum -- order -- suborder -- foo,
         * 
         *  yielding equal path lengths but different order. a correct solution
         *  would need to merge the paths, giving
         * 
         *  phylum -- subphylum -- order -- suborder -- foo.
         */
        int curDepth = -1;
        NodeI<T> deepestNode = null;
        for (NodeI<T> node : tree.getNodes()) {
            if (node.getDepth() > curDepth) {
                deepestNode = node;
                curDepth = node.getDepth();
            }
        }

        AttributeTypeI[] ret = new AttributeTypeI[curDepth + 1];
        int i = 0;
        for (NodeI<T> n : deepestNode.getPath()) {
            ret[i++] = n.getAttribute().getAttributeType();
        }
        return ret;
    }

    private interface ContentCloner<T> {

        T cloneContent(T content);
    }

    private interface DataMerger<T, U> {

        T merge(T first, U second);

        T getDefault();
    }

    private interface ContentAccessor<T, U> {

        T getContent(NodeI<U> in);
    }

    private static class LongCloner implements ContentCloner<Long> {

        @Override
        public Long cloneContent(Long content) {
            return content;
        }
    }

    private static class NodeAccess<T> implements ContentAccessor<T, T> {

        @Override
        public T getContent(NodeI<T> in) {
            return in.getContent();
        }
    }

    private static class PairNodeAccess<V, U> implements ContentAccessor<Pair<V, U>, U> {

        private V first;

        public void setFirst(V first) {
            this.first = first;
        }

        @Override
        public Pair<V, U> getContent(NodeI<U> in) {
            return new Pair<>(first, in.getContent());
        }
    }

    private static class Adder implements DataMerger<Long, Long> {

        @Override
        public Long merge(Long first, Long second) {
            return first + second;
        }

        @Override
        public Long getDefault() {
            return 0L;
        }
    }

    private static class Combiner<U, V> implements DataMerger<Map<U, V>, Pair<U, V>> {

        @Override
        public Map<U, V> merge(Map<U, V> first, Pair<U, V> second) {
            first.put(second.getFirst(), second.getSecond());
            return first;
        }

        @Override
        public Map<U, V> getDefault() {
            return new LinkedHashMap<>();
        }

        public Pair<U, V> getPair(U u, V v) {
            return new Pair<>(u, v);
        }
    }
}
