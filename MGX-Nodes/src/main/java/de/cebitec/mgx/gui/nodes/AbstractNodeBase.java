package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.ModelBaseI;
import java.awt.EventQueue;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
public abstract class AbstractNodeBase<T extends Identifiable<T>> extends AbstractNode implements Comparable<AbstractNodeBase<? extends T>>, PropertyChangeListener {

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
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canCopy() {
        return true;
    }

//    @Override
//    public boolean canDestroy() {
//        return true;
//    }
//
//    @Override
//    public void destroy() throws IOException {
//        content.removePropertyChangeListener(this);
//        super.destroy();
//    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case ModelBaseI.OBJECT_DELETED:
                if (evt.getSource().equals(content)) {
                    content.removePropertyChangeListener(this);
                    if (!EventQueue.isDispatchThread()) {
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                fireNodeDestroyed();
                            }
                        });
                    } else {
                        fireNodeDestroyed();
                    }
                }
                break;
            case ModelBaseI.OBJECT_MODIFIED:
                if (evt.getSource().equals(content)) {
                    updateModified();
                }
                break;
            default:
                System.err.println(getClass().getSimpleName() + " in AbstractNodeBase got unhandled event: " + evt.getPropertyName());
            //assert false;
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
