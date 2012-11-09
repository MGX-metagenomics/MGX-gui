package de.cebitec.mgx.gui.nodes;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.nodefactory.FileNodeFactory;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sjaenick
 */
public class MGXFileNode extends MGXNodeBase<MGXFile> {

    private FileNodeFactory nf = null;

    public MGXFileNode(MGXMaster m, MGXFile d, FileNodeFactory nf) {
        super(Children.create(nf, true), Lookups.fixed(m, d), d);
        this.nf = nf;
        setDisplayName(stripPath(d.getName()));
        setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
    }

    public MGXFileNode(MGXMaster m, MGXFile f) {
        super(Children.LEAF, Lookups.fixed(m, f), f);
        setDisplayName(stripPath(f.getName()));
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
        setDisplayName(stripPath(getContent().getName()));
        if (getContent().isDirectory()) {
            setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/Directory.png");
        } else {
            setIconBaseWithExtension("de/cebitec/mgx/gui/nodes/File.png");
        }
    }
}
