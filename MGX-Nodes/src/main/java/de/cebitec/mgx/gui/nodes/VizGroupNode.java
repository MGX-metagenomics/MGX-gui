/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.gui.nodefactory.GroupedSeqRunNodeFactory;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class VizGroupNode extends AbstractNodeBase<VisualizationGroupI> {

    private final VisualizationGroupI vGroup;

    public VizGroupNode(VisualizationGroupI vGroup) {
        super(Children.create(new GroupedSeqRunNodeFactory(vGroup), false), Lookups.singleton(vGroup), vGroup);
        this.vGroup = vGroup;
        setName(vGroup.getName());
        setDisplayName(vGroup.getName());
        vGroup.addPropertyChangeListener(this);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new RemoveVGroupAction()};
    }

//    @Override
//    public void childrenAdded(NodeMemberEvent ev) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void childrenRemoved(NodeMemberEvent ev) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void childrenReordered(NodeReorderEvent ev) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void nodeDestroyed(NodeEvent ev) {
//        //nf.removeNode(n);
//        //fireNodeDestroyed();
//    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case VisualizationGroupI.VISGROUP_RENAMED:
                setName((String) evt.getNewValue());
                setDisplayName((String) evt.getNewValue());
                break;
            case ModelBaseI.OBJECT_DELETED:
                getContent().removePropertyChangeListener(this);
                fireNodeDestroyed();
                break;
        }
    }

    @Override
    public void updateModified() {
        setDisplayName(getContent().getName());
        setShortDescription(getContent().getName());
    }

    private class RemoveVGroupAction extends AbstractAction {

        public RemoveVGroupAction() {
            putValue(NAME, "Remove");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // FIXME
        }
    }

}
