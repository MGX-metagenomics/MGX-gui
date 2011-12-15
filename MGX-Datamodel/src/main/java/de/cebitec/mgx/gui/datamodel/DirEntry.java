package de.cebitec.mgx.gui.datamodel;

/**
 *
 * @author sjaenick
 */
public class DirEntry {

    protected MGXMasterI master;
    protected MGXFile file = null;
    protected MGXDirectory directory = null;

    public MGXMasterI getMaster() {
        return master;
    }

    public void setMaster(MGXMasterI m) {
        this.master = m;
    }

    public MGXDirectory getDirectory() {
        return directory;
    }

    public void setDirectory(MGXDirectory directory) {
        this.directory = directory;
    }

    public MGXFile getFile() {
        return file;
    }

    public void setFile(MGXFile file) {
        this.file = file;
    }

    public boolean isFile() {
        return file != null && directory == null;
    }

    public boolean isDirectory() {
        return directory != null && file == null;
    }
}
