package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.datamodel.Habitat;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author sj
 */
public class HabitatNode extends MGXNodeBase<Habitat> {
    
    public HabitatNode(Children children, Lookup lookup) {
        super(children, lookup);
    }
    
    @Override
    public boolean canDestroy() {
        return true;
    }
    
    @Override
    public void destroy() throws IOException {
        super.destroy();
        fireNodeDestroyed();
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
        }
    }    
}
