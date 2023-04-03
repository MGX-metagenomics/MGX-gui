/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.nodeactions;

import de.cebitec.mgx.api.groups.ReplicateI;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.Collection;
import javax.swing.AbstractAction;
import org.openide.util.Utilities;

/**
 *
 * @author sjaenick
 */
public class RemoveReplicateAction extends AbstractAction {

    @Serial
    private static final long serialVersionUID = 1L;

    public RemoveReplicateAction() {
        super.putValue(NAME, "Remove replicate");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<? extends ReplicateI> toRemove = Utilities.actionsGlobalContext().lookupAll(ReplicateI.class);
        for (ReplicateI repl : toRemove) {
            repl.getReplicateGroup().remove(repl);
        }
    }

}
