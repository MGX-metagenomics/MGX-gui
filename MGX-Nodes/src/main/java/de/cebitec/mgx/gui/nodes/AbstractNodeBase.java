package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.model.ModelBaseI;
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
 * @param <T> object
 */
public abstract class AbstractNodeBase<T extends ModelBaseI<T>> extends AbstractNode implements Comparable<AbstractNodeBase<? extends T>>, PropertyChangeListener {

    private final T content;

    protected AbstractNodeBase(Children children, Lookup lookup, T data) {
        super(children, lookup);
        content = data;
        content.addPropertyChangeListener(this);
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
            case ModelBaseI.OBJECT_DELETED:
                content.removePropertyChangeListener(this);
                fireNodeDestroyed();
                break;
            case ModelBaseI.OBJECT_MODIFIED:
                updateModified();
                break;
            default:
                System.err.println("AbstractNodeBase got unhandled event: " + evt.getPropertyName());
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
    public int compareTo(AbstractNodeBase<? extends T> o) {
        return content.compareTo(o.getContent());
    }
}
