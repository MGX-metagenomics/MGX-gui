package de.cebitec.mgx.gui.datamodel.tree;

import de.cebitec.mgx.gui.datamodel.Attribute;
import de.cebitec.mgx.gui.datamodel.AttributeType;
import java.util.*;

/**
 *
 * @author sjaenick
 */
public class Tree<T> {

    Map<Long, Node<T>> nodes;
    Map<Long, Long> edges; // pointing upwards, from child to parent
    Map<Attribute, Node<T>> attrs;
    Map<String, List<Node<T>>> byRank;
    long id = 1;
    private Node<T> root = null;

    Tree() {
        edges = new HashMap<>();
        nodes = new HashMap<>();
        attrs = new HashMap<>();
        byRank = new HashMap<>();
    }

    public Node<T> createNode(Attribute attr, T content) {
        Node<T> node = new Node<>(this, id++, attr, content);
        if (nodes.containsValue(node)) {
            Node<T> tmp = getNode(content);
            return tmp;
        }
        attrs.put(attr, node);
        nodes.put(node.getId(), node);
        return node;
    }

    public void setRoot(Node<T> n) {
        root = n;
    }

    public Node<T> byId(long id) {
        return nodes.get(id);
    }

    public Node<T> byAttribute(Attribute attr) {
        return attrs.get(attr);
    }

    public void addEdge(Long child, Long parent) {
        //assert nodes.containsKey(child);
        //assert nodes.containsKey(parent);
        edges.put(child, parent);
    }

    public Node<T> getParent(long nodeId) {
        return byId(edges.get(nodeId));
    }

//    public int getDepth(Node<T> node) {
//        assert root != null;
//        assert node != null;
//        
//        if (node.isRoot()) {
//            return 0;
//        } else {
//            return 1 + getDepth(node.getParent());
//        }
//    }
    public Node<T> getRoot() {
        return root;
    }

    public Collection<Node<T>> getNodes() {
        return nodes.values();
    }

    public Collection<Node<T>> getNodesByAttributeType(AttributeType aType) {
        return byRank.get(aType.getName());
    }

    public Node<T> getNode(T content) {
        for (Node<T> n : nodes.values()) {
            if (n.getContent().equals(content)) {
                return n;
            }
        }
        return null;
    }

    public boolean containsNode(T content) {
        for (Node<T> n : nodes.values()) {
            if (n.getContent().equals(content)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public void build() {
        for (Node<T> node : nodes.values()) {
            node.build();
            
            String rank = node.getAttribute().getAttributeType().getName();
            if (!byRank.containsKey(rank)) {
                byRank.put(rank, new ArrayList<Node<T>>());
            }
            byRank.get(rank).add(node);
        }
    }
}
