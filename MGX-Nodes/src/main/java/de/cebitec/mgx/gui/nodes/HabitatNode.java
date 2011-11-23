package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.datamodel.Habitat;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author sj
 */
public class HabitatNode extends MGXNodeBase<Habitat> {

    public HabitatNode(Children children, Lookup lookup) {
        super(children, lookup);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
                    Utilities.actionsForPath("Actions/Habitat/").get(0),
                    Utilities.actionsForPath("Actions/Habitat/").get(1)
                };
    }
}