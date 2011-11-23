package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.client.MGXMaster;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public abstract class MGXNodeBase<T> extends AbstractNode {

    private MGXMaster master;
    private T dto;
    protected List<Action> actions = new ArrayList<Action>();

    public MGXNodeBase(Children children, Lookup lookup) {
        super(children, lookup);
    }

    public void setMaster(MGXMaster m) {
        master = m;
    }

    public MGXMaster getMaster() {
        return master;
    }

    public void setDTO(T dto) {
        this.dto = dto;
    }

    public T getDTO() {
        return dto;
    }

    @Override
    public Action[] getActions(boolean popup) {
        return actions.toArray(new Action[0]);
    }
}
