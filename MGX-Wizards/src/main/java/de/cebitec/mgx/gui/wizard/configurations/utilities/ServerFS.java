package de.cebitec.mgx.gui.wizard.configurations.utilities;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author sjaenick
 */
public class ServerFS extends FileSystemView {

    private final MGXMaster master;

    public ServerFS(MGXMaster m) {
        this.master = m;
    }

    @Override
    public File createNewFolder(File containingDir) throws IOException {
        return null;
    }

    @Override
    public boolean isRoot(File f) {
        ServerFile file = (ServerFile) f;
        return file.isRoot();
    }

    @Override
    public Boolean isTraversable(File f) {
        return Boolean.TRUE;
    }

    @Override
    public String getSystemDisplayName(File f) {
        return super.getSystemDisplayName(f);
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
        return super.getRoots();
    }

    @Override
    public File getHomeDirectory() {
        return getRoots()[0];
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
        return super.getFiles(dir, useFileHiding);
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
