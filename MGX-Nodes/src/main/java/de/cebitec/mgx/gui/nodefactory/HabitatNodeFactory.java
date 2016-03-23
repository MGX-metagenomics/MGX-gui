package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.gui.nodes.HabitatNode;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class HabitatNodeFactory extends MGXNodeFactoryBase<HabitatI> {

    public HabitatNodeFactory(MGXMasterI m) {
        super(m);
    }

    @Override
    protected boolean addKeys(List<HabitatI> toPopulate) {
        try {
            Iterator<HabitatI> iter = getMaster().Habitat().fetchall();
            while (iter.hasNext()) {
                toPopulate.add(iter.next());
            }
            Collections.sort(toPopulate);
            return true;
        } catch (MGXException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(HabitatI key) {
        HabitatNode node = new HabitatNode(key);
        node.addNodeListener(this);
        return node;
    }
}
