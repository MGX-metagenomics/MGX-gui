package de.cebitec.mgx.gui.nodes.actions;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */

public class HabitatDeleteAction extends AbstractAction {

    public HabitatDeleteAction() {
        putValue(NAME, "Delete");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        assert (SwingUtilities.isEventDispatchThread());

        // FIXME - swingworker?

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                // FIXME: ask for confirmation

//                try {
//                    getMaster().Habitat().delete(getDTO());
//                } catch (MGXServerException ex) {
//                    Exceptions.printStackTrace(ex);
//                } catch (MGXClientException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
            }
        });
    }
}