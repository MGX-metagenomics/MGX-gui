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
import de.cebitec.mgx.gui.dtoconversion.FileDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.io.File;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.UUID;
import org.openide.util.Exceptions;

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
    public boolean createDirectory(MGXFileI newObj) throws MGXException {
        FileDTO dto = FileDTOFactory.getInstance().toDTO(newObj);
        try {
            return 1 == dtomaster.File().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            if (ex.getMessage().trim().endsWith("already exists.")) {
                throw new MGXException(ex);
            }
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    @Override
    public Iterator<MGXFileI> fetchall(final MGXFileI curDir) {
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
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public TaskI delete(MGXFileI obj) {
        TaskI t = null;
        try {
            FileDTO dto = FileDTOFactory.getInstance().toDTO(obj);
            UUID uuid = dtomaster.File().delete(dto);
            t = getMaster().Task().get(obj, uuid, TaskI.TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return t;
    }

    @Override
    public Iterator<MGXFileI> fetchall() {
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
            return new UploadBaseI() {

                @Override
                public boolean upload() {
                    return up.upload();
                }

                @Override
                public long getNumElementsSent() {
                    return up.getProgress();
                }
            };
        } catch (MGXClientException ex) {
            throw new MGXException(ex);
        }
    }

    @Override
    public DownloadBaseI createDownloader(String serverFname, OutputStream out) {
        try {
            final FileDownloader fd = dtomaster.File().createDownloader(serverFname, out);
            return new DownloadBaseI() {

                @Override
                public boolean download() {
                    return fd.download();
                }

                @Override
                public long getProgress() {
                    return fd.getProgress();
                }
            };
        } catch (MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public DownloadBaseI createPluginDumpDownloader(OutputStream out) {
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

}
