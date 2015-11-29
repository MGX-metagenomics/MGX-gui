/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.api.groups.ReplicateI;
import de.cebitec.mgx.api.groups.VisualizationGroupI;
import de.cebitec.mgx.api.model.ModelBaseI;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class ReplicateNode extends VizGroupNode {

    public ReplicateNode(ReplicateI replicate) {
        super(replicate);
        setName(replicate.getName());
        setDisplayName(replicate.getName());
        replicate.addPropertyChangeListener(this);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new RemoveReplicateAction()};
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

    private class RemoveReplicateAction extends AbstractAction {

        public RemoveReplicateAction() {
            putValue(NAME, "Remove");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // FIXME
            ReplicateI replicate = Utilities.actionsGlobalContext().lookup(ReplicateI.class);
            assert replicate != null;
            replicate.getReplicateGroup().remove(replicate);
        }
    }

}
