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

    protected MGXNodeBase(Children children, Lookup lookup, T data) {
        super(children, lookup, data);
    }

    @Override
    public final Action getPreferredAction() {
        return null;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return new Action[0]; // disables context menu
    }
}
