package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.ModelBase;
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
 */
public abstract class MGXNodeBase<T extends ModelBase> extends AbstractNode implements PropertyChangeListener {

    protected MGXMaster master;
    protected T content;

    protected MGXNodeBase(Children children, Lookup lookup, T data) {
        super(children, lookup);
        content = data;
        content.addPropertyChangeListener(this);
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
        System.err.println("node got PCE: " + evt.getPropertyName());
        switch (evt.getPropertyName()) {
            case ModelBase.OBJECT_DELETED:
                content.removePropertyChangeListener(this);
                fireNodeDestroyed();
                break;
            case ModelBase.OBJECT_MODIFIED:
                updateModified();
                break;
            default:
                System.err.println("unhandled event: " + evt.getPropertyName());
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
}
