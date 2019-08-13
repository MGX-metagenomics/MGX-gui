package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.assembly.AssemblyI;
import de.cebitec.mgx.gui.nodes.AssemblyNode;
import java.util.Iterator;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class AssemblyNodeFactory extends MGX2NodeFactoryBase<MGXMasterI, AssemblyI> {

    public AssemblyNodeFactory(MGXMasterI m) {
        super(m);
    }

    @Override
    protected boolean addKeys(List<AssemblyI> toPopulate) {
        try {
            Iterator<AssemblyI> iter = getMaster().Assembly().fetchall();
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
    protected AssemblyNode createNodeFor(AssemblyI key) {
        return new AssemblyNode(getMaster(), key);
    }
}
