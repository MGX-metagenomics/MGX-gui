package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.Habitat;
import de.cebitec.mgx.gui.nodefactory.SampleNodeFactory;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sj
 */
public class HabitatNode extends MGXNodeBase {

    private SampleNodeFactory snf = null;
    
    public HabitatNode(MGXMaster m, Habitat h) {
        this(h, new SampleNodeFactory(m, h));
        master = m;
        setDisplayName(h.getName());
    }

    private HabitatNode(Habitat h, SampleNodeFactory snf) {
        super(Children.create(snf, true), Lookups.singleton(h));
        this.snf = snf;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{new DeleteHabitat()};
    }

    private class DeleteHabitat extends AbstractAction {

        public DeleteHabitat() {
            putValue(NAME, "Delete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Habitat hab = getLookup().lookup(Habitat.class);
            getMaster().Habitat().delete(hab.getId());
            fireNodeDestroyed();
        }
    }
}
