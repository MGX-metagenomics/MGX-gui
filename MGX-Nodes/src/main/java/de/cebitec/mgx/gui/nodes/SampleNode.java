package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.datamodel.Sample;
import java.io.IOException;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author sj
 */
public class SampleNode extends MGXNodeBase<Sample> {

    public SampleNode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public SystemAction[] getActions() {
        return super.getActions();
    }

    @Override
    public void destroy() throws IOException {
        super.destroy();
        fireNodeDestroyed();
    }
}
