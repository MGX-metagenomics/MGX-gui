package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Sample;
import de.cebitec.mgx.gui.nodefactory.DNAExtractNodeFactory;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class SampleNode extends MGXNodeBase {

    private DNAExtractNodeFactory nf = null;

    public SampleNode(MGXMaster m, Sample s) {
        this(s, new DNAExtractNodeFactory(m, s));
        master = m;
        setDisplayName(s.getMaterial());
    }

    private SampleNode(Sample s, DNAExtractNodeFactory snf) {
        super(Children.create(snf, true), Lookups.singleton(s));
        this.nf = snf;
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
