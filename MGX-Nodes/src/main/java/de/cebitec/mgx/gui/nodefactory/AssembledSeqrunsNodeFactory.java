package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.AssembledSeqRunI;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.gui.nodes.AssembledSeqRunNode;
import java.util.Iterator;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class AssembledSeqrunsNodeFactory extends MGX2NodeFactoryBase<AssemblyI, AssembledSeqRunI> {

    public AssembledSeqrunsNodeFactory(AssemblyI a) {
        super(a);
    }

    @Override
    protected boolean addKeys(List<AssembledSeqRunI> toPopulate) {
        try {
            Iterator<AssembledSeqRunI> iter = getMaster().SeqRun().ByAssembly(getContent());
            while (iter.hasNext()) {
                toPopulate.add(iter.next());
            }
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    @Override
    protected AssembledSeqRunNode createNodeFor(AssembledSeqRunI key) {
        return new AssembledSeqRunNode(key);
    }
}
