package de.cebitec.mgx.gui.swingutils.util;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.model.MGXFileI;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class ServerFS extends FileSystemView {

    private final MGXMasterI master;
    /**
     * Stellt das StartVerzeichnis in der View dar.
     */
    private ServerFile projectDirectory = null;

    public ServerFS(MGXMasterI m) {
        this.master = m;
        projectDirectory = new ServerFile(MGXFileI.getRoot(master));
    }

    @Override
    public File createNewFolder(File containingDir) throws IOException {
        return null;
    }

    @Override
    public boolean isRoot(File f) {
        return projectDirectory == f;
    }

    @Override
    public Boolean isTraversable(File f) {
        return f.isDirectory();
    }
    
    @Override
    public String getSystemDisplayName(File f) {
        String ret = super.getSystemDisplayName(f);
        return ret != null ? ret : f.getName();
    }

    @Override
    public String getSystemTypeDescription(File f) {
        return super.getSystemTypeDescription(f);
    }

    @Override
    public Icon getSystemIcon(File f) {
        return super.getSystemIcon(f);
    }

    @Override
    public boolean isParent(File folder, File file) {
        return super.isParent(folder, file);
    }

    @Override
    public File getChild(File parent, String fileName) {
        return super.getChild(parent, fileName);
    }

    @Override
    public boolean isFileSystem(File f) {
        return isRoot(f);
    }

    @Override
    public boolean isHiddenFile(File f) {
        return false;
    }

    @Override
    public boolean isFileSystemRoot(File dir) {
        return isRoot(dir);
    }

    @Override
    public boolean isDrive(File dir) {
        return isRoot(dir);
    }

    @Override
    public boolean isFloppyDrive(File dir) {
        return false;
    }

    @Override
    public boolean isComputerNode(File dir) {
        return isRoot(dir);
    }

    @Override
    public File[] getRoots() {
        return new File[]{projectDirectory};
    }

    @Override
    public File getHomeDirectory() {
        return projectDirectory;
    }

    @Override
    public File getDefaultDirectory() {
        return getRoots()[0];
    }

    @Override
    public File createFileObject(File dir, String filename) {
        return super.createFileObject(dir, filename);
    }

    @Override
    public File createFileObject(String path) {
        return super.createFileObject(path);
    }

    @Override
    public File[] getFiles(File dir, boolean useFileHiding) {
        ServerFile[] children = null;
        if (dir instanceof ServerFile) {

            ServerFile file = (ServerFile) dir;
            Iterator<MGXFileI> filesIter = null;
            try {
                filesIter = master.File().fetchall(file.getMGXFile());
            } catch (MGXException ex) {
                Exceptions.printStackTrace(ex);
            }
            List<MGXFileI> files = new ArrayList<>();
            while (filesIter != null && filesIter.hasNext()) {
                files.add(filesIter.next());
            }
            
            children = new ServerFile[files.size()];

            for (int i = 0; i < files.size(); i++) {
                children[i] = new ServerFile(files.get(i));
            }
            return children;

        } else {
            return new ServerFile[0];
        }
    }

    @Override
    public File getParentDirectory(File dir) {
        return super.getParentDirectory(dir);
    }

    @Override
    protected File createFileSystemRoot(File f) {
        return super.createFileSystemRoot(f);
    }
}
