package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.datamodel.DNAExtract;
import java.io.IOException;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author sj
 */
public class DNAExtractNode extends MGXNodeBase<DNAExtract> {

    public DNAExtractNode(Children children, Lookup lookup) {
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
