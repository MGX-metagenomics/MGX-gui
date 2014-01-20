package de.cebitec.mgx.gui.util;

import de.cebitec.mgx.gui.datamodel.MGXFile;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public class ServerFile extends File {

    private final MGXFile file;

    public ServerFile(MGXFile file) {
        super(file.getFullPath());
        this.file = file;
    }

    @Override
    public String getName() {
        String fname = file.getName();
        if (fname.contains(MGXFile.separator)) {
            fname = fname.substring(fname.lastIndexOf(MGXFile.separator)+1);
        }
        if (MGXFile.ROOT_PATH.equals(fname)) {
            return "Project storage";
        }
        return fname;
    }

    @Override
    public String getParent() {
        if (file.getParent() != null) {
            return file.getParent().getFullPath();
        } else {
            return null;
        }
    }

    @Override
    public File getParentFile() {
        if (file.getParent() != null) {
            return new ServerFile(file.getParent());
        } else {
            return null;
        }
    }

    @Override
    public String getPath() {
        return file.getFullPath();
    }

    @Override
    public String getAbsolutePath() {
        return file.getName();
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public boolean isFile() {
        return !file.isDirectory();
    }

    @Override
    public String toString() {
        return file.getName();
    }

    @Override
    public long length() {
        return file.getSize();
    }

    public boolean isRoot() {
        return file.getParent() == null;
    }

    @Override
    public File getCanonicalFile() throws IOException {
        if (file.isDirectory()) {
            return this;
        } else {
            return super.getCanonicalFile();
        }
    }

    public MGXFile getMGXFile() {
        return file;
    }
}
