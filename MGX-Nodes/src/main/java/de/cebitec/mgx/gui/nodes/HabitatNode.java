package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.datamodel.Habitat;
import java.io.IOException;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.actions.EditAction;
import org.openide.actions.NewAction;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;

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
        NewAction newa = SystemAction.get(NewAction.class);
        EditAction edit = SystemAction.get(EditAction.class);
        DeleteAction delete = SystemAction.get(DeleteAction.class);
        return new Action[] {newa, edit, delete};
    }

    @Override
    public NewType[] getNewTypes() {
        NewType newType = new NewType() {

            @Override
            public String getName() {
                return "sample";
            }

            @Override
            public void create() throws IOException {
                System.err.println("doing stuff");
                //Do Something
            }
        };
        return new NewType[]{newType};
    }
}