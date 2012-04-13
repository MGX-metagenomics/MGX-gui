package de.cebitec.mgx.gui.datamodel.tree;

import de.cebitec.mgx.gui.datamodel.Attribute;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author sjaenick
 */
public class Node<T> {

    private long id;
    private Attribute attr;
    private T value;
    private Tree<T> tree;
    private Set<Node<T>> children = null;

    public Node(Tree<T> tree, long id, Attribute attr, T content) {
        this.tree = tree;
        this.id = id;
        this.attr = attr;
        this.value = content;
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
    
    public Node<T> getParent() {
        return tree.getParent(id);
    }

    public Set<Node<T>> getChildren() {
        return children;
    }
    
    void build() {
        children = new HashSet<Node<T>>();
        for (Entry<Long, Long> e : tree.edges.entrySet()) {
            Long from = e.getKey();
            Long to = e.getValue();
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
        final Node other = (Node) obj;
        
        return this.attr.equals(other.attr);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 19 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }
}
