package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.model.ModelBaseI;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.PasteType;

/**
 *
 * @author sjaenick
 * @param <T> object
 */
public abstract class MGXNodeBase<T extends ModelBaseI<T>> extends AbstractNode implements Comparable<MGXNodeBase<? extends T>>, PropertyChangeListener {

    private final T content;

    protected MGXNodeBase(Children children, Lookup lookup, T data) {
        super(children, lookup);
        content = data;
        content.addPropertyChangeListener(this);

        getSheet();
    }

    public final T getContent() {
        return content;
    }

    @Override
    public final Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    protected final void createPasteTypes(Transferable t, List<PasteType> s) {
        super.createPasteTypes(t, s);
        PasteType paste = getDropType(t, DnDConstants.ACTION_REFERENCE, -1);
        if (paste != null) {
            s.add(paste);
        }
    }

    @Override
    public PasteType getDropType(Transferable t, int action, int index) {
        return null;
//        java.util.List<PasteType> s = new LinkedList<>();
//        createPasteTypes(t, s);
//        return s.isEmpty() ? null : s.get(0);
    }

    @Override
    public final boolean canCut() {
        return false;
    }

    @Override
    public final boolean canCopy() {
        return true;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public void destroy() throws IOException {
        System.err.println("destroy(): " + this);
        super.destroy();
        content.removePropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ModelBaseI.OBJECT_DELETED:
                if (evt.getSource() == content) {
                    //
                    // own content marked as deleted; detach listener
                    //
                    content.removePropertyChangeListener(this);
                }
                break;
            case ModelBaseI.OBJECT_MODIFIED:
                if (evt.getSource() == content) {
                    //
                    // own content changed; update display name etc.
                    //
                    updateModified();
                }
                break;
            case ModelBaseI.CHILD_CHANGE:
                // NOP
                break;
            default:
                System.err.println(getClass().getSimpleName() + " in AbstractNodeBase got unhandled event: " + evt.getPropertyName() + " from " + evt.getSource());
            //assert false;
        }
    }

    @Override
    public final Action getPreferredAction() {
        return null;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
    }

    public final void addPropertyChangelistener(PropertyChangeListener pcl) {
        //super.addPropertyChangeListener(pcl);
        content.addPropertyChangeListener(pcl);
    }

    public final void removePropertyChangelistener(PropertyChangeListener pcl) {
        //super.removePropertyChangeListener(pcl);
        content.removePropertyChangeListener(pcl);
    }

    public abstract void updateModified();

    @Override
    public final int compareTo(MGXNodeBase<? extends T> o) {
        return content.compareTo(o.getContent());
    }
}
