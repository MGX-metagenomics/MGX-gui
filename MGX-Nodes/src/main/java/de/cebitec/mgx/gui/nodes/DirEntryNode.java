package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.datamodel.DirEntry;
import de.cebitec.mgx.gui.nodefactory.DirEntryNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class DirEntryNode extends MGXNodeBase {

    private DirEntryNodeFactory nf = null;

    public DirEntryNode(DirEntry d, DirEntryNodeFactory nf) {
        super(Children.create(nf, true), Lookups.singleton(d));
        this.nf = nf;
        setDisplayName(stripPath(d.getDirectory().getName()));
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
    }

    public DirEntryNode(DirEntry d) {
        super(Children.LEAF, Lookups.singleton(d));
        setDisplayName(stripPath(d.getFile().getName()));
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/File.png");
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{};
    }
    
    private static String stripPath(String in) {
        String[] split = in.split("/");
        return split[split.length-1];
    }
}
