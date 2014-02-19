package de.cebitec.mgx.gui.datamodel.tree;

import de.cebitec.mgx.gui.datamodel.Attribute;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class Node<T> {

    private final long id;
    private final Attribute attr;
    private T value;
    private final Tree<T> tree;
    private Set<Node<T>> children = null;

    public Node(Tree<T> tree, long id, Attribute attr, T content) {
        this.tree = tree;
        this.id = id;
        this.attr = attr;
        this.value = content;
        //children = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public Attribute getAttribute() {
        return attr;
    }

    public T getContent() {
        return value;
    }

    public void setContent(T value) {
        this.value = value;
    }

    public boolean isRoot() {
        return this == tree.getRoot();
    }

    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }

    public Node<T> getParent() {
        return tree.getParent(id);
    }

    public Node<T>[] getPath() {
        //System.err.println("get path for " + getAttribute().getValue());
        Node<T>[] ret = new Node[getDepth() + 1];
        if (isRoot()) {
            ret[getDepth()] = this;
        } else {
            Node<T> parent = getParent();
            assert parent != null;
            Node<T>[] parentPath = parent.getPath();
            int i = 0;
            for (Node<T> curNode : parentPath) {
                ret[i++] = curNode;
            }
            ret[getDepth()] = this;
        }
        return ret;
    }

    public int getDepth() {
        if (isRoot()) {
            return 0;
        }
        return 1 + getParent().getDepth();
    }

    public Set<Node<T>> getChildren() {
        return children;
    }
    
    public boolean hasChildren() {
        return !isLeaf();
    }
    
    public Node<T> addChild(Attribute attr, T content) {
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
        final Node<T> other = (Node<T>) obj;
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
