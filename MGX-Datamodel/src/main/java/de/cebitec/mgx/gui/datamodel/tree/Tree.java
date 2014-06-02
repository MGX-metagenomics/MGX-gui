package de.cebitec.mgx.gui.datamodel.tree;

import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import de.cebitec.mgx.api.model.tree.TreeI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class Tree<T> implements TreeI<T> {

    private final Map<Long, NodeI<T>> nodes;
    Map<Long, Long> edges; // pointing upwards, from child to parent
    long id = 1;
    private NodeI<T> root = null;

    public Tree() {
        edges = new HashMap<>();
        nodes = new HashMap<>();
    }

    @Override
    public NodeI<T> createRootNode(AttributeI attr, T content) {
        NodeI<T> node = new Node<>(this, id++, attr, content);
        nodes.put(node.getId(), node);
        assert root == null; // only one root node
        root = node;
        return node;
    }

    Node<T> addNode(Node<T> parent, AttributeI attr, T content) {
        Node<T> node = new Node<>(this, id++, attr, content);
        nodes.put(node.getId(), node);
        edges.put(node.getId(), parent.getId());
        return node;
    }

    @Override
    public int size() {
        return nodes.size();
    }

    NodeI<T> byId(long id) {
        return nodes.get(id);
    }

    NodeI<T> getParent(long nodeId) {
        Long parentId = edges.get(nodeId);
        if (parentId == null) {
            return null;
        }
        return byId(parentId);
    }

    @Override
    public NodeI<T> getRoot() {
        return root;
    }

    @Override
    public Collection<NodeI<T>> getNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }
    
    @Override
    public Collection<NodeI<T>> getLeaves() {
        Collection<NodeI<T>> ret = new HashSet<>();
        for (NodeI<T> n : getNodes()) {
            if (n.isLeaf()) {
                ret.add(n);
            }
        }
        return ret;
    }
}
