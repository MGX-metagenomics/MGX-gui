package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.api.model.assembly.BinI;
import de.cebitec.mgx.gui.nodes.BinNode;
import java.util.Iterator;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class BinNodeFactory extends MGX2NodeFactoryBase<AssemblyI, BinI> {

    public BinNodeFactory(AssemblyI a) {
        super(a);
    }

    @Override
    protected boolean addKeys(List<BinI> toPopulate) {
        try {
            Iterator<BinI> iter = getMaster().Bin().ByAssembly(getContent());
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
    protected BinNode createNodeFor(BinI key) {
        return new BinNode(key);
    }
}
