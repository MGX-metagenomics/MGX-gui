package de.cebitec.mgx.gui.datamodel.tree;

import de.cebitec.mgx.gui.datamodel.Attribute;
import java.util.*;

/**
 *
 * @author sjaenick
 */
public class Tree<T> {

    private Map<Long, Node<T>> nodes;
    Map<Long, Long> edges; // pointing upwards, from child to parent
    long id = 1;
    private Node<T> root = null;

    Tree() {
        edges = new HashMap<>();
        nodes = new HashMap<>();
    }

    public Node<T> createRootNode(Attribute attr, T content) {
        Node<T> node = new Node<>(this, id++, attr, content);
        nodes.put(node.getId(), node);
        assert root == null; // only one root node
        root = node;
        return node;
    }

    Node<T> addNode(Node<T> parent, Attribute attr, T content) {
        Node<T> node = new Node<>(this, id++, attr, content);
        nodes.put(node.getId(), node);
        edges.put(node.getId(), parent.getId());
        return node;
    }

    int size() {
        return nodes.size();
    }

    Node<T> byId(long id) {
        return nodes.get(id);
    }

    Node<T> getParent(long nodeId) {
        Long parentId = edges.get(nodeId);
        if (parentId == null) {
            return null;
        }
        return byId(parentId);
    }

    public Node<T> getRoot() {
        return root;
    }

    public Collection<Node<T>> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
