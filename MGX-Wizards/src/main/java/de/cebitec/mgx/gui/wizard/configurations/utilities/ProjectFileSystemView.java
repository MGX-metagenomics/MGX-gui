package de.cebitec.mgx.gui.wizard.configurations.utilities;

import de.cebitec.mgx.gui.datamodel.DirEntry;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * Erweitert die FileSystemView und stellt die Dateien des Projekts dar.
 *
 *
 * @author belmann
 */
public class ProjectFileSystemView extends FileSystemView {

    /**
     * Map fuer alle Dateien die in der View enthalten sein sollen.
     */
    private Map files;
    /**
     * Stellt das StartVerzeichnis in der View dar.
     */
    private ProjectFile projectDirectory = null;
    /**
     * Stellt den Namen des StartVerszeichnisses (projectDirectory) dar.
     */
    private String projectDirectoryName = null;

    
    /**
     * 
     * Initialisiert die Root Dateien, sowie alle anderen Dateien, die in der
     * View vorhanden sein sollen.
     * 
     * @param entries Eintraege der Dateien.
     */
    public ProjectFileSystemView(List<DirEntry> entries) {
        files = new HashMap();
        if (entries.size() > 0) {
            if (entries.get(0).isDirectory()) {

                projectDirectoryName = entries.get(0).getDirectory().getName();

            } else {

                projectDirectoryName = entries.get(0).getFile().getName();

            }
        }
        String[] names = projectDirectoryName.split("/");
        projectDirectoryName = names[0];

        projectDirectory = new ProjectFile(projectDirectoryName, false);
        files.put(projectDirectoryName, projectDirectory);

        ProjectFile[] filesProject = new ProjectFile[entries.size()];
        for (int i = 0; i < entries.size(); i++) {

            if (entries.get(i).isFile()) {
                filesProject[i] = new ProjectFile(entries.get(i).
                        getFile().getName(), true);
            } else {
                filesProject[i] = new ProjectFile(entries.get(i).
                        getDirectory().getName(), false);
            }
        }

        files.put("getFiles(" + projectDirectoryName + ")", filesProject);
        files.put("getRoots", new File[]{(File) projectDirectory});
        this.setPath(entries);
    }

    
    /**
     * Setzt den Pfad der Dateien die in der View angezeigt werden sollen.
     * @param entries Eintraege in der View.
     */
    private void setPath(List<DirEntry> entries) {
        for (int i = 0; i < entries.size(); i++) {
            boolean isDirectory = entries.get(i).isDirectory();
            if (isDirectory) {
                String newPath = entries.get(i).getDirectory().getName();
                ProjectFile file = new ProjectFile(newPath,
                        !isDirectory);
                files.put(newPath, file);

                ProjectFile[] children = new ProjectFile[entries.get(i).
                        getDirectory().getEntries().size()];

                for (int j = 0; j < children.length; j++) {

                    if (entries.get(i).getDirectory().
                            getEntries().get(j).isDirectory()) {
                        ProjectFile fileDirectory = new ProjectFile(entries.get(i).
                                getDirectory().getEntries().get(j).getDirectory().getName(),
                                !entries.get(i).getDirectory().
                                getEntries().get(j).isDirectory());
                        children[j] = fileDirectory;
                    } else {
                        ProjectFile fileFile = new ProjectFile(entries.get(i).
                                getDirectory().getEntries().get(j).getFile().getName(),
                                !entries.get(i).getDirectory().
                                getEntries().get(j).isDirectory());
                        children[j] = fileFile;
                    }
                }

                files.put("getFiles(" + entries.get(i).getDirectory().getName() + ")", children);

                setPath(entries.get(i).getDirectory().getEntries());
            } else {
                String newPath = entries.get(i).getFile().getName();
                ProjectFile file = new ProjectFile(newPath,
                        !isDirectory);
                files.put(newPath, file);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#createNewFolder(java.io.File)
     */
    @Override
    public File createNewFolder(File containingDir) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.swing.filechooser.FileSystemView#createFileObject(java.io.File,
     * java.lang.String)
     */
    @Override
    public File createFileObject(File dir, String filename) {
        return super.createFileObject(dir, filename);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.swing.filechooser.FileSystemView#createFileObject(java.lang.String)
     */
    @Override
    public File createFileObject(String path) {
        return super.createFileObject(path);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.swing.filechooser.FileSystemView#createFileSystemRoot(java.io.File)
     */
    @Override
    protected File createFileSystemRoot(File f) {
        return createFileSystemRoot(f);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#getChild(java.io.File,
     * java.lang.String)
     */
    @Override
    public File getChild(File parent, String fileName) {
        return super.getChild(parent, fileName);
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#getDefaultDirectory()
     */
    @Override
    public File getDefaultDirectory() {
        return projectDirectory;
    }
    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#getFiles(java.io.File,
     * boolean)
     */
    @Override
    public File[] getFiles(File dir, boolean useFileHiding) {
        ProjectFile[] children = null;
        if (dir instanceof ProjectFile) {
            ProjectFile file = (ProjectFile) dir;
            children = (ProjectFile[]) files.get("getFiles(" + file.getCanonicalPath() + ")");
        }

        if (children == null) {
            return new File[0];
        } else {
            return children;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#getHomeDirectory()
     */
    @Override
    public File getHomeDirectory() {
        return projectDirectory;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.swing.filechooser.FileSystemView#getParentDirectory(java.io.File)
     */
    @Override
    public File getParentDirectory(File dir) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#getRoots()
     */
    @Override
    public File[] getRoots() {
        return (File[]) files.get("getRoots");
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * javax.swing.filechooser.FileSystemView#getSystemDisplayName(java.io.File)
     */

    @Override
    public String getSystemDisplayName(File f) {
        return f.getName();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#getSystemIcon(java.io.File)
     */
    @Override
    public Icon getSystemIcon(File f) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.swing.filechooser.FileSystemView#getSystemTypeDescription(java.io.File)
     */
    @Override
    public String getSystemTypeDescription(File f) {
        return "Description";
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#isFileSystem(java.io.File)
     */
    @Override
    public boolean isFileSystem(File f) {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.swing.filechooser.FileSystemView#isFileSystemRoot(java.io.File)
     */
    @Override
    public boolean isFileSystemRoot(File dir) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#isHiddenFile(java.io.File)
     */
    @Override
    public boolean isHiddenFile(File f) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#isParent(java.io.File,
     * java.io.File)
     */
    @Override
    public boolean isParent(File folder, File file) {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#isRoot(java.io.File)
     */
    @Override
    public boolean isRoot(File f) {
        return projectDirectory == f;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.filechooser.FileSystemView#isTraversable(java.io.File)
     */
    @Override
    public Boolean isTraversable(File f) {
        return Boolean.TRUE;
    }
}
