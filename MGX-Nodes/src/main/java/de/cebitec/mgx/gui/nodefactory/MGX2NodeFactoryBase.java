package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.Identifiable;
import de.cebitec.mgx.api.model.MGXDataModelBaseI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.gui.nodes.MGXNodeBase;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author sjaenick
 * @param <T> type of object represented by encapsulating node
 * @param <U> type of object represented by children
 *
 */
public abstract class MGX2NodeFactoryBase<T extends MGXDataModelBaseI<T>, U extends Identifiable<U>> extends ChildFactory<U> implements PropertyChangeListener {

    private final T myObj;

    public MGX2NodeFactoryBase(T myobj) {
        this.myObj = myobj;
        if (myObj != null) {
            myObj.addPropertyChangeListener(this);
        }
    }

    protected final T getContent() {
        return myObj;
    }

    protected final MGXMasterI getMaster() {
        return myObj != null ? myObj.getMaster() : null;
    }

    @Override
    protected final synchronized boolean createKeys(List<U> toPopulate) {
        if (getMaster() == null || getMaster().isDeleted()) {
            toPopulate.clear();
            return true;
        } else {
            try {
                boolean ret = addKeys(toPopulate);
                Collections.sort(toPopulate);
                return ret;
            } catch (MGXLoggedoutException mle) {
                toPopulate.clear();
            }
            return true;
        }
    }

    protected abstract boolean addKeys(List<U> toPopulate);
    
    @Override
    protected final Node createNodeForKey(U key) {
        MGXNodeBase<U> node = createNodeFor(key);
        //node.addNodeListener(this);
        key.addPropertyChangeListener(this);
        return node;
    }

    protected abstract MGXNodeBase<U> createNodeFor(U key);

    @Override
    public final void propertyChange(PropertyChangeEvent evt) {

        Object src = evt.getSource();

        switch (evt.getPropertyName()) {
            case Node.PROP_PARENT_NODE:
                //ignore
                break;
            case ModelBaseI.OBJECT_DELETED:
                if (src instanceof ModelBaseI) {
                    ModelBaseI<?> modelObj = (ModelBaseI) src;
                    modelObj.removePropertyChangeListener(this);

                    if (modelObj == myObj) {
                        // deletion of own node ongoing, nothing to do
                    } else {
                        // a child has been deleted, need to refresh
                        //
                        // do NOT refresh immediately, multiple objects might be
                        // deleted at once
                        //
                        refresh(false);
                    }
                } else {
                    System.err.println("MGXNodeFactoryBase: got unhandled foreign " + evt.toString() + " in " + getClass().getName());
                }
                break;
            case ModelBaseI.OBJECT_MODIFIED:
                //
                // a child instance was modified, nothing to do
                //
                break;
            case ModelBaseI.CHILD_CHANGE:
                if (src == myObj) {
                    refresh(false);
                } else {
                    //
                    // one of our children has a change in the number of its children
                    //
                }
                break;
            default:
                System.err.println("MGXNodeFactoryBase: " + evt.toString() + " in " + getClass().getName());
                break;
        }
    }
}
