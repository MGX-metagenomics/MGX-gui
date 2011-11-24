package de.cebitec.mgx.gui.nodes.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

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