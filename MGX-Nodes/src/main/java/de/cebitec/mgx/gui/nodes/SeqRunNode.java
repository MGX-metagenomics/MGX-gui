package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.SeqRun;
import java.io.IOException;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class SeqRunNode extends MGXNodeBase {

    public SeqRunNode(MGXMaster m, SeqRun s) {
        this(s);
        master = m;
        setDisplayName(s.getSequencingMethod() + " run");
    }

    private SeqRunNode(SeqRun s) {
        super(Children.LEAF, Lookups.singleton(s));
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public SystemAction[] getActions() {
        return super.getActions();
    }
}
