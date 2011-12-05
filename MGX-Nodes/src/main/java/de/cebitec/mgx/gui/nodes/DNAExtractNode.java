package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.DNAExtract;
import de.cebitec.mgx.gui.nodefactory.SeqRunNodeFactory;
import java.io.IOException;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class DNAExtractNode extends MGXNodeBase {

    private SeqRunNodeFactory snf = null;

    public DNAExtractNode(MGXMaster m, DNAExtract d) {
        this(d, new SeqRunNodeFactory(m, d));
        master = m;
        setDisplayName(d.getMethod());
    }

    private DNAExtractNode(DNAExtract d, SeqRunNodeFactory snf) {
        super(Children.create(snf, true), Lookups.singleton(d));
        this.snf = snf;
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
