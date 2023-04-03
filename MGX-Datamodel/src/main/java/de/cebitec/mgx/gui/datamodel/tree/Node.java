package de.cebitec.mgx.gui.datamodel.tree;

import de.cebitec.mgx.api.model.AttributeI;
import de.cebitec.mgx.api.model.tree.NodeI;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class Node<T> implements NodeI<T> {

    private final long id;
    private final AttributeI attr;
    private T value;
    private final Tree<T> tree;
    private Set<NodeI<T>> children = null;

    public Node(Tree<T> tree, long id, AttributeI attr, T content) {
        this.tree = tree;
        this.id = id;
        this.attr = attr;
        this.value = content;
        //children = new HashSet<>();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public AttributeI getAttribute() {
        return attr;
    }

    @Override
    public T getContent() {
        return value;
    }

    @Override
    public void setContent(T value) {
        this.value = value;
    }

    @Override
    public boolean isRoot() {
        return this == tree.getRoot();
    }

    @Override
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }

    @Override
    public NodeI<T> getParent() {
        return tree.getParent(id);
    }

    @Override
    public NodeI<T>[] getPath() {
        //System.err.println("get path for " + getAttribute().getValue());
        @SuppressWarnings("unchecked")
        NodeI<T>[] ret = new NodeI[getDepth() + 1];
        if (isRoot()) {
            ret[getDepth()] = this;
        } else {
            NodeI<T> parent = getParent();
            assert parent != null;
            NodeI<T>[] parentPath = parent.getPath();
            int i = 0;
            for (NodeI<T> curNode : parentPath) {
                ret[i++] = curNode;
            }
            ret[getDepth()] = this;
        }
        return ret;
    }

    @Override
    public int getDepth() {
        if (isRoot()) {
            return 0;
        }
        return 1 + getParent().getDepth();
    }

    @Override
    public Set<NodeI<T>> getChildren() {
        return children;
    }

    @Override
    public boolean hasChildren() {
        return !isLeaf();
    }

    @Override
    public Node<T> addChild(AttributeI attr, T content) {
        Node<T> child = tree.addNode(this, attr, content);
        if (children == null) {
            children = new HashSet<>();
        }
        children.add(child);
        return child;
    }

    void build() {
        children = new HashSet<>();
        for (Entry<Long, Long> e : tree.edges.entrySet()) {
            Long from = e.getKey();
            Long to = e.getValue();
            assert from != null;
            assert to != null;
            if (id == to.longValue()) {
                children.add(tree.byId(from));
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node<?> other = (Node) obj;
        if ((this.id == other.id) && (this.tree == other.tree)) {
            return true;
        }
        if (!Objects.equals(this.attr, other.attr)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 19 * hash + Objects.hashCode(this.attr);
        hash = 19 * hash + Objects.hashCode(this.value);
        return hash;
    }
}
