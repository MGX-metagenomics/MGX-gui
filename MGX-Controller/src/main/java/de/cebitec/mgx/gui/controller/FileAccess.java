package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.FileAccessI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.FileDownloader;
import de.cebitec.mgx.client.datatransfer.FileUploader;
import de.cebitec.mgx.client.datatransfer.PluginDumpDownloader;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.dtoconversion.FileDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author sjaenick
 */
public class FileAccess extends MasterHolder implements FileAccessI {

    public FileAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public boolean createDirectory(MGXFileI targetDir, String name) throws MGXException {
        // check name
        String[] invalid = new String[]{"|", "/", "\\", ".."};

        for (String s : invalid) {
            if (name.contains(s)) {
                throw new MGXException("Invalid character: " + s);
            }
        }

        if (!targetDir.isDirectory()) {
            throw new MGXException("Selected parent " + targetDir.getName() + " is not a directory.");
        }

        String targetPath = targetDir.getFullPath() + MGXFileI.separator + name;
        final MGXFileI newDir = new MGXFile(getMaster(), targetPath, true, 0);
        newDir.setParent(targetDir);

        FileDTO dto = FileDTOFactory.getInstance().toDTO(newDir);
        try {
            return 1 == getDTOmaster().File().create(dto);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<MGXFileI> fetchall(final MGXFileI curDir) throws MGXException {
        try {
            Iterator<FileDTO> fetchall = getDTOmaster().File().fetchall(curDir.getFullPath()).getFileList().iterator();
            return new BaseIterator<FileDTO, MGXFileI>(fetchall) {
                @Override
                public MGXFileI next() {
                    MGXFileI f = FileDTOFactory.getInstance().toModel(curDir.getMaster(), iter.next());
                    return f;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex.getMessage());
        }
    }

    @Override
    public TaskI<MGXFileI> delete(MGXFileI obj) throws MGXException {
        TaskI<MGXFileI> t = null;
        try {
            FileDTO dto = FileDTOFactory.getInstance().toDTO(obj);
            UUID uuid = getDTOmaster().File().delete(dto);
            t = getMaster().<MGXFileI>Task().get(obj, uuid, TaskI.TaskType.DELETE);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return t;
    }

    @Override
    public Iterator<MGXFileI> fetchall() throws MGXException {
        return fetchall(MGXFileI.getRoot(getMaster()));
    }

    @Override
    public UploadBaseI createUploader(File localFile, MGXFileI targetDir, String targetName) throws MGXException {

        if (localFile.isDirectory()) {
            throw new MGXException(localFile.getName() + " is a directory, cannot upload.");
        }
        if (!targetDir.isDirectory()) {
            throw new MGXException("Selected parent " + targetDir.getName() + " is not a directory.");
        }

        if (targetName.contains("/")) {
            throw new MGXException("Invalid character in name: \"/\".");
        }
        String fullPath = targetDir.getFullPath() + MGXFileI.separator + targetName;
        try {
            final FileUploader up = getDTOmaster().File().createUploader(localFile, fullPath);
            return new ServerFileUploader(targetDir, up);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public DownloadBaseI createDownloader(String serverFname, OutputStream out) throws MGXException {
        try {
            final FileDownloader fd = getDTOmaster().File().createDownloader(serverFname, out);
            return new ServerFileDownloader(fd);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public DownloadBaseI createPluginDumpDownloader(OutputStream out) throws MGXException {
        try {
            final PluginDumpDownloader pd = getDTOmaster().File().createPluginDumpDownloader(out);
            return new ServerPluginDumpDownloader(pd);
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

    private static class ServerPluginDumpDownloader extends DownloadBaseI implements PropertyChangeListener {

        private final PluginDumpDownloader fd;

        public ServerPluginDumpDownloader(PluginDumpDownloader fd) {
            this.fd = fd;
            fd.addPropertyChangeListener(this);
        }

        @Override
        public boolean download() {
            boolean ret = fd.download();
            fd.removePropertyChangeListener(this);
            if (!ret) {
                setErrorMessage(fd.getErrorMessage());
            }
            return ret;
        }

        @Override
        public long getProgress() {
            return fd.getProgress();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            fireTaskChange(evt.getPropertyName(), fd.getProgress());
        }

    }

    private static class ServerFileDownloader extends DownloadBaseI implements PropertyChangeListener {

        private final FileDownloader fd;

        public ServerFileDownloader(FileDownloader fd) {
            this.fd = fd;
            fd.addPropertyChangeListener(this);
        }

        @Override
        public boolean download() {
            boolean ret = fd.download();
            fd.removePropertyChangeListener(this);
            if (!ret) {
                setErrorMessage(fd.getErrorMessage());
            }
            return ret;
        }

        @Override
        public long getProgress() {
            return fd.getProgress();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            fireTaskChange(evt.getPropertyName(), fd.getProgress());
        }

    }

    private class ServerFileUploader extends UploadBaseI implements PropertyChangeListener {

        private final FileUploader fu;
        private final MGXFileI targetDir;

        public ServerFileUploader(MGXFileI targetDir, FileUploader fu) {
            this.fu = fu;
            this.targetDir = targetDir;
            fu.addPropertyChangeListener(this);
        }

        @Override
        public boolean upload() {
            boolean ret = fu.upload();
            fu.removePropertyChangeListener(this);
            if (!ret) {
                setErrorMessage(fu.getErrorMessage());
            } else {
                targetDir.modified();
            }
            return ret;
        }

        @Override
        public long getNumElementsSent() {
            return fu.getProgress();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            fireTaskChange(evt.getPropertyName(), fu.getProgress());
        }

    }

}
