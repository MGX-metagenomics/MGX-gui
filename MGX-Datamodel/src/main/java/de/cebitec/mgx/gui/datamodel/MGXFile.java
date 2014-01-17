package de.cebitec.mgx.gui.datamodel;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author sjaenick
 */
public class MGXFile extends ModelBase implements Comparable<MGXFile> {

    protected  MGXFile parent;
    protected final String fullPath;
    protected final boolean isDirectory;
    //
    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MGXFile.class, "MGXFile");
    public static final String ROOT_PATH = ".";
    public static final String separator = "|";

    public MGXFile(String path, boolean isDir) {
        super(DATA_FLAVOR); 
        if (!path.startsWith(".")) {
            throw new RuntimeException(path + " is invalid");
        }
        this.fullPath = path;
        this.isDirectory = isDir;
    }

    public static MGXFile getRoot(MGXMasterI m) {
        MGXFile root = new MGXFile(ROOT_PATH, true);
        root.setParent(null);
        root.setMaster(m);
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

    public String getName() {
        int sepPos = getFullPath().lastIndexOf(separator);
        return fullPath.substring(sepPos+1);
    }

    public String getFullPath() {
        return fullPath;
    }

    @Override
    public int compareTo(MGXFile o) {
        if (isDirectory == o.isDirectory()) {
            return this.fullPath.compareTo(o.fullPath);
        } else {
            if (isDirectory) {
                return -1;
            }
            return 1;
        }
    }
}
