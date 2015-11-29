/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.groups.ReplicateGroupI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.ModelBaseI;
import de.cebitec.mgx.gui.nodefactory.ReplicateNodeFactory;
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
public class ReplicateGroupNode extends AbstractNodeBase<ReplicateGroupI> {


    public ReplicateGroupNode(ReplicateGroupI rGroup) {
        super(Children.create(new ReplicateNodeFactory(rGroup), false), Lookups.singleton(rGroup), rGroup);
        setName(rGroup.getName());
        setDisplayName(rGroup.getName());
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{};
    }

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    private class RemoveVGroupAction extends AbstractAction {
//
//        public RemoveVGroupAction() {
//            putValue(NAME, "Remove");
//        }
//
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            // FIXME
//        }
//    }

}
