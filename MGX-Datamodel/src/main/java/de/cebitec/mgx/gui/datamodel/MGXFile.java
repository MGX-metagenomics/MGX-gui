package de.cebitec.mgx.gui.datamodel;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.model.MGXFileI;

/**
 *
 * @author sjaenick
 */
public class MGXFile extends MGXFileI {

    protected MGXFileI parent;
    protected final String fullPath;
    protected final boolean isDirectory;
    protected final long size;
    //

    public MGXFile(MGXMasterI m, String path, boolean isDir, long size) {
        super(m);
        if (!path.startsWith(MGXFileI.ROOT_PATH)) {
            throw new RuntimeException(path + " is invalid");
        }
        this.fullPath = path;
        this.isDirectory = isDir;
        this.size = size;
    }

    @Override
    public void setParent(MGXFileI parent) {
        this.parent = parent;
    }

    @Override
    public MGXFileI getParent() {
        return parent;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public String getName() {
        int sepPos = getFullPath().lastIndexOf(separator);
        return fullPath.substring(sepPos + 1);
    }

    @Override
    public String getFullPath() {
        return fullPath;
    }

    @Override
    public int compareTo(MGXFileI o) {
        if (isDirectory() == o.isDirectory()) {
            return this.getFullPath().compareTo(o.getFullPath());
        } else {
            if (isDirectory()) {
                return -1;
            }
            return 1;
        }
    }
}
