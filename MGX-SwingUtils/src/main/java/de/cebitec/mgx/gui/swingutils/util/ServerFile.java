package de.cebitec.mgx.gui.swingutils.util;

import de.cebitec.mgx.api.model.MGXFileI;
import java.io.File;
import java.io.IOException;
import java.io.Serial;

/**
 *
 * @author sjaenick
 */
public class ServerFile extends File {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private final MGXFileI file;

    public ServerFile(MGXFileI file) {
        super(file.getFullPath());
        this.file = file;
    }

    @Override
    public String getName() {
        String fname = file.getName();
        if (fname.contains(MGXFileI.separator)) {
            fname = fname.substring(fname.lastIndexOf(MGXFileI.separator)+1);
        }
        if (MGXFileI.ROOT_PATH.equals(fname)) {
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

    public MGXFileI getMGXFile() {
        return file;
    }
}
