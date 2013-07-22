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

    private static <T, U, V> void addChildren(Node<T> parent, Set<Node<U>> children, ContentAccessor<V, U> cac, DataMerger<T, V> merger) {
        for (Node<U> node : children) {
            Node<T> correctChild = null;

            if (!parent.isLeaf()) {
                for (Node<T> candidate : parent.getChildren()) {
                    if (nodesAreEqual(node, candidate)) {
                        correctChild = candidate;
                        V content = cac.getContent(node);
                        correctChild.setContent(merger.merge(correctChild.getContent(), content));
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
    private static <T, U> boolean nodesAreEqual(Node<T> n1, Node<U> n2) {
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

    private interface DataMerger<T, U> {

        T merge(T first, U second);

        T getDefault();
    }

    private interface ContentAccessor<T, U> {

        T getContent(Node<U> in);
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
