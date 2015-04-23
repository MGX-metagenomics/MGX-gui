package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ModelBase;
import java.awt.datatransfer.Transferable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 * @param <T> datamodel object
 */
public abstract class MGXNodeBase<T extends ModelBase<T>> extends AbstractNode implements Comparable<MGXNodeBase<? extends T>>, PropertyChangeListener {

    private final MGXMasterI master;
    private final T content;

    protected MGXNodeBase(MGXMasterI master, Children children, Lookup lookup, T data) {
        super(children, lookup);
        if (master == null) {
            throw new IllegalArgumentException("null master supplied");
        }
        this.master = master;
        content = data;
        content.addPropertyChangeListener(this);
    }

    protected final MGXMasterI getMaster() {
        return master;
    }

    public final T getContent() {
        return content;
    }

    @Override
    public final Transferable drag() throws IOException {
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

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
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
    public int compareTo(MGXNodeBase<? extends T> o) {
        return content.compareTo(o.getContent());
    }
}
