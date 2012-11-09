package de.cebitec.mgx.gui.wizard.configurations.utilities;

import de.cebitec.mgx.gui.datamodel.MGXFile;
import java.io.File;

/**
 *
 * @author sjaenick
 */
public class ServerFile extends File {
    
    private final MGXFile file;

    public ServerFile(MGXFile file) {
        super(file.getName());
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getParent() {
        return file.getParent().getName();
    }

    @Override
    public File getParentFile() {
        return new ServerFile(file.getParent());
    }

    @Override
    public String getPath() {
        return file.getFullPath();
    }

    @Override
    public String getAbsolutePath() {
        return file.getFullPath();
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
        return file.getFullPath();
    }
    
    public boolean isRoot() {
        return file.getParent() == null;
    }
}
