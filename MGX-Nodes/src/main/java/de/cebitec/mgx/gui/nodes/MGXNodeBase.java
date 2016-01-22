package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.ModelBaseI;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 * @param <T> datamodel object
 */
public abstract class MGXNodeBase<T extends ModelBaseI<T>> extends AbstractNodeBase<T> {

    private final MGXMasterI master;

    protected MGXNodeBase(MGXMasterI master, Children children, Lookup lookup, T data) {
        super(children, lookup, data);
        if (master == null) {
            throw new IllegalArgumentException("null master supplied");
        }
        this.master = master;
    }

    protected final MGXMasterI getMaster() {
        return master;
    }

//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        super.propertyChange(evt);
//    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
    }

//    public void addPropertyChangelistener(PropertyChangeListener pcl) {
//        super.addPropertyChangeListener(pcl);
//        content.addPropertyChangeListener(pcl);
//    }
//
//    public void removePropertyChangelistener(PropertyChangeListener pcl) {
//        super.removePropertyChangeListener(pcl);
//        content.removePropertyChangeListener(pcl);
//    }


}
