package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sjaenick
 */
public class MGXFile extends ModelBase implements Comparable<MGXFile> {

    protected MGXFile parent = null;
    protected String name;
    protected boolean isDirectory;
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MGXFile.class, "MGXFile");

    public MGXFile() {
        super(DATA_FLAVOR);
    }

    public static MGXFile getRoot(MGXMasterI m) {
        MGXFile root = new MGXFile();
        root.setMaster(m);
        root.setName("");
        root.isDirectory(true);
        return root;
    }

    public void setParent(MGXFile parent) {
        this.parent = parent;
    }

    public MGXFile getParent() {
        return parent;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void isDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullPath() {
        if (parent != null) {
            return parent.getFullPath() + "/" + getName();
        }
        return "." + getName();
    }

    @Override
    public int compareTo(MGXFile o) {
        if (isDirectory == o.isDirectory()) {
            return this.name.compareTo(o.name);
        } else {
            if (isDirectory) {
                return -1;
            }
            return 1;
        }
    }
}
