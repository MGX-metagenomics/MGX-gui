package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.DirEntry;
import de.cebitec.mgx.gui.nodefactory.DirEntryNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class DirEntryNode extends MGXNodeBase<DirEntry> {

    private DirEntryNodeFactory nf = null;

    public DirEntryNode(MGXMaster m, DirEntry d, DirEntryNodeFactory nf) {
        super(Children.create(nf, true), Lookups.fixed(m, d), d);
        this.nf = nf;
        setDisplayName(stripPath(d.getDirectory().getName()));
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
    }

    public DirEntryNode(MGXMaster m, DirEntry d) {
        super(Children.LEAF, Lookups.fixed(m, d), d);
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
        return split[split.length - 1];
    }

    @Override
    public void updateModified() {
        setDisplayName(stripPath(getContent().getFile().getName()));
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/File.png");
    }
}
