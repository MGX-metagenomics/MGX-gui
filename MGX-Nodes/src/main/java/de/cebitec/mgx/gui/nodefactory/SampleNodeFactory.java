package de.cebitec.mgx.gui.nodefactory;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.HabitatI;
import de.cebitec.mgx.api.model.SampleI;
import de.cebitec.mgx.gui.nodes.SampleNode;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Exceptions;

/**
 *
 * @author sj
 */
public class SampleNodeFactory extends ChildFactory<SampleI> implements NodeListener {

    private final MGXMasterI master;
    private final HabitatI habitat;

    public SampleNodeFactory(HabitatI h) {
        this.master = h.getMaster();
        this.habitat = h;
    }

    @Override
    protected boolean createKeys(List<SampleI> toPopulate) {
        try {
            Iterator<SampleI> iter = master.Sample().ByHabitat(habitat);
            while (iter != null && iter.hasNext()) {
                if (Thread.interrupted()) {
                    master.log(Level.INFO, "interrupted in NF");
                    return true;
                }
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
    protected Node createNodeForKey(SampleI key) {
        SampleNode node = new SampleNode(key);
        node.addNodeListener(this);
        return node;
    }

    public void refreshChildren() {
        refresh(true);
    }

    @Override
    public void childrenAdded(NodeMemberEvent ev) {
        refresh(true);
    }

    @Override
    public void childrenRemoved(NodeMemberEvent ev) {
        refresh(true);
    }

    @Override
    public void childrenReordered(NodeReorderEvent ev) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ev) {
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //refresh(true);
    }
}
