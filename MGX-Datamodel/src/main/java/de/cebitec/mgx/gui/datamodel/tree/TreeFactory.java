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

    public static <T> void dumpNode(Node<? extends T> node, int offset) {
        for (int i = 0; i < offset; i++) {
            System.err.print(" ");
        }
        System.err.println(node.getAttribute().toString());
        if (node.hasChildren()) {
            for (Node<? extends T> n : node.getChildren()) {
                dumpNode(n, offset + 5);
            }
        }
    }

    public static <T> Tree<T> createTree(final Map<Attribute, T> map) {

        Checker.sanityCheck(map.keySet());

        Map<Long, Node<T>> idmap = new HashMap<>(map.size()); // attr id to node
        Map<Attribute, T> disconnected = new HashMap<>();

        Tree<T> tree = new Tree<>();
        for (Entry<Attribute, T> entry : map.entrySet()) {
            Attribute attr = entry.getKey();
            if (attr.getParentID() == Identifiable.INVALID_IDENTIFIER) {
                Node<T> root = tree.createRootNode(attr, entry.getValue());
                idmap.put(attr.getId(), root);
            } else if (idmap.containsKey(attr.getParentID())) {
                Node<T> parent = idmap.get(attr.getParentID());
                Node<T> child = parent.addChild(attr, entry.getValue());
                idmap.put(child.getAttribute().getId(), child);
            } else {
                disconnected.put(attr, entry.getValue());
            }
        }

        while (!disconnected.isEmpty()) {
            Iterator<Entry<Attribute, T>> it = disconnected.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Attribute, T> next = it.next();
                Attribute attr = next.getKey();
                if (idmap.containsKey(attr.getParentID())) {
                    Node<T> parent = idmap.get(attr.getParentID());
                    Node<T> child = parent.addChild(attr, disconnected.get(attr));
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

    public static <T> Tree<T> mergeTrees(Collection<Tree<T>> trees) {
        Tree<T> consensus = new Tree<>();
        DataMerger<T, T> adder = new Adder();

        for (Tree<T> t : trees) {
            Node<T> root = consensus.getRoot();
            if (root == null) {
                root = consensus.createRootNode(t.getRoot().getAttribute(), t.getRoot().getContent());
            } else {
                //root.setContent(root.getContent().longValue() + t.getRoot().getContent().longValue());
                root.setContent(adder.merge(root.getContent(), t.getRoot().getContent()));
            }
            ContentAccessor<T, T> cac = new NodeAccess<>();
            addChildren(root, t.getRoot().getChildren(), cac, adder);
        }
        return consensus;
    }

//    public static <T> Tree<T> filterTree(Tree<T> tree, Set<Attribute> exclude) {
//        Tree<T> clone = new Tree<>();
//
//        // clone root node
//        long rootContent = tree.getRoot().getContent().longValue();
//        Node<Long> cloneRoot = clone.createRootNode(tree.getRoot().getAttribute(), Long.valueOf(rootContent));
//
//        // clone children recursively
//        cloneChildren(cloneRoot, tree.getRoot().getChildren(), new LongCloner());
//
//        return clone;
//    }
    public static Tree<Long> createKRONATree(Tree<Long> tree) {

        tree = TreeFactory.clone(tree);

        // for KRONA plots, we need each nodes count to be the number
        // of reads most specifically assigned to this node only, excluding
        // reads assigned to a more specific entry.
        // Thus, we iterate over all nodes and subtract the sum of reads assigned
        // to the immediate child nodes.
        Map<Attribute, Long> newContent = new HashMap<>(tree.getNodes().size());
        for (Node<Long> node : tree.getNodes()) {
            Long numPathsEndingHere = node.getContent();
            if (!node.isLeaf()) {
                numPathsEndingHere = numPathsEndingHere - nodeSum(node.getChildren());
            }
            newContent.put(node.getAttribute(), numPathsEndingHere);
        }

        // ..and update
        for (Node<Long> node : tree.getNodes()) {
            node.setContent(newContent.get(node.getAttribute()));
        }
        return tree;
    }

    public static Tree<Long> filter(Tree<Long> tree, Set<Attribute> exclude) {
        Tree<Long> clone = new Tree<>();

        // clone root node
        if (!exclude.contains(tree.getRoot().getAttribute())) {
            long rootContent = tree.getRoot().getContent().longValue();
            Node<Long> cloneRoot = clone.createRootNode(tree.getRoot().getAttribute(), Long.valueOf(rootContent));

            // filter children recursively
            filterChildren(cloneRoot, tree.getRoot().getChildren(), new LongCloner(), exclude);
        }
        
        return clone;
    }

    private static <T> void filterChildren(Node<T> parent, Set<Node<T>> children, ContentCloner<T> cloner, Set<Attribute> exclude) {
        for (Node<T> child : children) {
            if (!exclude.contains(child.getAttribute())) {
                Node<T> clonedChild = parent.addChild(child.getAttribute(), cloner.cloneContent(child.getContent()));
                if (!child.isLeaf()) {
                    filterChildren(clonedChild, child.getChildren(), cloner, exclude);
                }
            }
        }
    }

    public static Tree<Long> clone(Tree<Long> tree) {
        Tree<Long> clone = new Tree<>();

        // clone root node
        long rootContent = tree.getRoot().getContent().longValue();
        Node<Long> cloneRoot = clone.createRootNode(tree.getRoot().getAttribute(), Long.valueOf(rootContent));

        // clone children recursively
        cloneChildren(cloneRoot, tree.getRoot().getChildren(), new LongCloner());

        return clone;
    }

    private static <T> void cloneChildren(Node<T> parent, Set<Node<T>> children, ContentCloner<T> cloner) {
        for (Node<T> child : children) {
            Node<T> clonedChild = parent.addChild(child.getAttribute(), cloner.cloneContent(child.getContent()));
            if (!child.isLeaf()) {
                cloneChildren(clonedChild, child.getChildren(), cloner);
            }
        }
    }

    private static <T, U, V> void addChildren(Node<T> parent, Set<Node<U>> children, ContentAccessor<V, U> cac, DataMerger<T, V> merger) {
        for (Node<U> node : children) {
            Node<T> correctChild = null;

            if (parent.hasChildren()) {
                for (Node<T> candidate : parent.getChildren()) {
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

    public static <U, V> Tree<Map<U, V>> combineTrees(List<Pair<U, Tree<V>>> trees) {

        Tree<Map<U, V>> combined = new Tree<>();
        DataMerger<Map<U, V>, Pair<U, V>> merger = new Combiner<>();
        PairNodeAccess<U, V> pna = new PairNodeAccess<>();
        ContentAccessor<Pair<U, V>, V> cac = pna;

        for (Pair<U, Tree<V>> pair : trees) {
            pna.setFirst(pair.getFirst());
            Tree<V> t = pair.getSecond();
            Node<Map<U, V>> root = combined.getRoot();
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
    public static <T, U> boolean nodesAreEqual(Node<T> n1, Node<U> n2) {
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

    private static long nodeSum(Set<Node<Long>> nodes) {
        int sum = 0;
        for (Node<Long> n : nodes) {
            sum += n.getContent();
        }
        return sum;
    }

    private interface ContentCloner<T> {

        T cloneContent(T content);
    }

    private interface DataMerger<T, U> {

        T merge(T first, U second);

        T getDefault();
    }

    private interface ContentAccessor<T, U> {

        T getContent(Node<U> in);
    }

    private static class LongCloner implements ContentCloner<Long> {

        @Override
        public Long cloneContent(Long content) {
            return Long.valueOf(content.longValue());
        }
    }

    private static class NodeAccess<T> implements ContentAccessor<T, T> {

        @Override
        public T getContent(Node<T> in) {
            return in.getContent();
        }
    }

    private static class PairNodeAccess<V, U> implements ContentAccessor<Pair<V, U>, U> {

        private V first;

        public void setFirst(V first) {
            this.first = first;
        }

        @Override
        public Pair<V, U> getContent(Node<U> in) {
            return new Pair<>(first, in.getContent());
        }
    }

    private static class Adder<T> implements DataMerger<Long, Long> {

        @Override
        public Long merge(Long first, Long second) {
            return first.longValue() + second.longValue();
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
