package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.gui.nodes.ReferenceNode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class ReferenceNodeFactory extends MGXNodeFactoryBase<MGXReferenceI> {

    public ReferenceNodeFactory(MGXMasterI master) {
        super(master);
    }

    @Override
    protected boolean addKeys(List<MGXReferenceI> toPopulate) {
        try {
            Iterator<MGXReferenceI> iter = getMaster().Reference().fetchall();
            while (iter.hasNext()) {
                toPopulate.add(iter.next());
            }
            Collections.sort(toPopulate);
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return false;
    }

    @Override
    protected Node createNodeFor(MGXReferenceI ref) {
        return new ReferenceNode(ref);
    }
}
