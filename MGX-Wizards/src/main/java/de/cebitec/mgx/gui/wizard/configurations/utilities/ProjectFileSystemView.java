package de.cebitec.mgx.gui.wizard.configurations.utilities;

import de.cebitec.mgx.gui.controller.MGXMaster;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public ProjectFileSystemView(List<MGXFile> entries) {
        files = new HashMap<>();
        if (entries.size() > 0) {
            projectDirectoryName = entries.get(0).getName();
        }
        String[] names = projectDirectoryName.split("/");
        projectDirectoryName = names[0];

        projectDirectory = new ProjectFile(projectDirectoryName, false);
        files.put(projectDirectoryName, projectDirectory);

        ProjectFile[] filesProject = new ProjectFile[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            MGXFile curElement = entries.get(i);
            filesProject[i] = new ProjectFile(curElement.getName(), !curElement.isDirectory());
        }

        files.put("getFiles(" + projectDirectoryName + ")", filesProject);
        files.put("getRoots", new File[]{(File) projectDirectory});
        this.setPath(entries);
    }

    /**
     * Setzt den Pfad der Dateien die in der View angezeigt werden sollen.
     *
     * @param entries Eintraege in der View.
     */
    private void setPath(List<MGXFile> entries) {
        for (int i = 0; i < entries.size(); i++) {
            MGXFile currentFile = entries.get(i);
            MGXMaster master = (MGXMaster) currentFile.getMaster();

            boolean isDirectory = currentFile.isDirectory();
            if (currentFile.isDirectory()) {

                // create directory entry
                String newPath = currentFile.getName();
                ProjectFile file = new ProjectFile(newPath, false);
                files.put(newPath, file);

                // process children
                List<MGXFile> dirEntries = master.File().fetchall(currentFile);
                ProjectFile[] children = new ProjectFile[dirEntries.size()];

                for (int j = 0; j < children.length; j++) {
                    MGXFile child = dirEntries.get(j);

                    ProjectFile pfEntry;

                    if (child.isDirectory()) {
                        pfEntry = new ProjectFile(child.getName(),
                                !child.isDirectory());
                    } else {
                        pfEntry = new ProjectFile(child.getName(),
                                !child.isDirectory());

                    }
                    children[j] = pfEntry;
                }

                files.put("getFiles(" + entries.get(i).getName() + ")", children);

                setPath(dirEntries);
            } else {
                String newPath = currentFile.getName();
                ProjectFile file = new ProjectFile(newPath, !isDirectory);
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
