package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ModelBase;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 * @param <T> datamodel object
 * @param <U> corresponding node type for datamodel object
 */
public abstract class MGXNodeBase<T extends ModelBase, U extends MGXNodeBase> extends AbstractNode implements PropertyChangeListener, Comparable<U> {

    private final MGXMasterI master;
    private final T content;

    protected MGXNodeBase(MGXMasterI master, Children children, Lookup lookup, T data) {
        super(children, lookup);
        this.master = master;
        content = data;
        content.addPropertyChangeListener(this);
    }
    
    protected MGXMasterI getMaster() {
        return master;
    }

    public T getContent() {
        return content;
    }

    @Override
    public Transferable drag() throws IOException {
        return content;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ModelBase.OBJECT_DELETED:
                content.removePropertyChangeListener(this);
                fireNodeDestroyed();
                break;
            case ModelBase.OBJECT_MODIFIED:
                updateModified();
                break;
            default:
                System.err.println("MGXNodeBase got unhandled event: " + evt.getPropertyName());
                assert false;
        }
    }

    public void addPropertyChangelistener(PropertyChangeListener pcl) {
        super.addPropertyChangeListener(pcl);
        content.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangelistener(PropertyChangeListener pcl) {
        super.removePropertyChangeListener(pcl);
        content.removePropertyChangeListener(pcl);
    }

    public abstract void updateModified();

    @Override
    public int compareTo(U o) {
        return content.compareTo(o.getContent());
    }
}
