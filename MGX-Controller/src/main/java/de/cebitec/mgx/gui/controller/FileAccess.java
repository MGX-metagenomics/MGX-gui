package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.FileAccessI;
import de.cebitec.mgx.api.access.datatransfer.DownloadBaseI;
import de.cebitec.mgx.api.access.datatransfer.UploadBaseI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.MGXFileI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.FileDownloader;
import de.cebitec.mgx.client.datatransfer.FileUploader;
import de.cebitec.mgx.client.datatransfer.PluginDumpDownloader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
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
public class FileAccess implements FileAccessI {

    private final MGXMasterI master;
    private final MGXDTOMaster dtomaster;

    public FileAccess(MGXMasterI master, MGXDTOMaster dtomaster) {
        this.master = master;
        this.dtomaster = dtomaster;
    }

    @Override
    public MGXMasterI getMaster() {
        return master;
    }

    @Override
    public boolean createDirectory(MGXFileI parent, String name) throws MGXException {
        // check name
        String[] invalid = new String[]{"|", "/", "\\", ".."};

        for (String s : invalid) {
            if (name.contains(s)) {
                throw new MGXException("Invalid character: " + s);
            }
        }

        String targetPath = parent.getFullPath() + MGXFileI.separator + name;
        final MGXFileI newDir = new MGXFile(getMaster(), targetPath, true, 0);
        newDir.setParent(parent);

        FileDTO dto = FileDTOFactory.getInstance().toDTO(newDir);
        try {
            return 1 == dtomaster.File().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public Iterator<MGXFileI> fetchall(final MGXFileI curDir) throws MGXException {
        try {
            Iterator<FileDTO> fetchall = dtomaster.File().fetchall(curDir.getFullPath());
            return new BaseIterator<FileDTO, MGXFileI>(fetchall) {
                @Override
                public MGXFileI next() {
                    MGXFileI f = FileDTOFactory.getInstance().toModel(curDir.getMaster(), iter.next());
                    return f;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public TaskI delete(MGXFileI obj) throws MGXException {
        TaskI t = null;
        try {
            FileDTO dto = FileDTOFactory.getInstance().toDTO(obj);
            UUID uuid = dtomaster.File().delete(dto);
            t = getMaster().Task().get(obj, uuid, TaskI.TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
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
        assert targetDir.isDirectory();
        if (targetName.contains("/")) {
            assert false;
        }
        String fullPath = targetDir.getFullPath() + MGXFileI.separator + targetName;
        try {
            final FileUploader up = dtomaster.File().createUploader(localFile, fullPath);
            return new ServerFileUploader(up);
        } catch (MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public DownloadBaseI createDownloader(String serverFname, OutputStream out) throws MGXException {
        try {
            final FileDownloader fd = dtomaster.File().createDownloader(serverFname, out);
            return new ServerFileDownloader(fd);
        } catch (MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public DownloadBaseI createPluginDumpDownloader(OutputStream out) throws MGXException {
        final PluginDumpDownloader pd = dtomaster.File().createPluginDumpDownloader(out);
        return new DownloadBaseI() {

            @Override
            public boolean download() {
                return pd.download();
            }

            @Override
            public long getProgress() {
                return pd.getProgress();
            }
        };
    }

    private class ServerFileDownloader extends DownloadBaseI implements PropertyChangeListener {

        private final FileDownloader fd;

        public ServerFileDownloader(FileDownloader fd) {
            this.fd = fd;
            fd.addPropertyChangeListener(this);
        }

        @Override
        public boolean download() {
            boolean ret = fd.download();
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

        public ServerFileUploader(FileUploader fu) {
            this.fu = fu;
            fu.addPropertyChangeListener(this);
        }

        @Override
        public boolean upload() {
            boolean ret = fu.upload();
            if (!ret) {
                setErrorMessage(fu.getErrorMessage());
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
