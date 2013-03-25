package de.cebitec.mgx.gui.wizard.configurations.utilities;

import de.cebitec.mgx.gui.datamodel.MGXFile;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author sjaenick
 */
public class ServerFile extends File {

    private MGXFile file;

    public ServerFile(MGXFile file) {
        super(file.getFullPath());
        this.file = file;
    }

    @Override
    public String getName() {
        return file.getName();
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

    @Override
    public File getCanonicalFile() throws IOException {

        if (file.isDirectory()) {
            return this;
        } else {
            return super.getCanonicalFile();
        }
    }

    /**
     * Getter fuer MGXFile
     *
     * @return MGXFile
     */
    public MGXFile getMGXFile() {
        return file;
    }

    /**
     * Setter fuer MGXFile
     *
     * @param file MGXFile
     */
    public void setMGXFile(MGXFile file) {
        this.file = file;
    }
}
