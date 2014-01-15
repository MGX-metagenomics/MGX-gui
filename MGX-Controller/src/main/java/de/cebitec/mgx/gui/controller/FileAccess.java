package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.datatransfer.FileUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.datamodel.misc.Task;
import de.cebitec.mgx.gui.dtoconversion.FileDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.io.File;
import java.util.Iterator;
import java.util.UUID;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class FileAccess extends AccessBase<MGXFile> {

    @Override
    public long create(MGXFile newObj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public boolean createDirectory(MGXFile newObj) {
        FileDTO dto = FileDTOFactory.getInstance().toDTO(newObj);
        try {
            return 1 == getDTOmaster().File().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    @Override
    public MGXFile fetch(long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Iterator<MGXFile> fetchall(final MGXFile curDir) {
        try {
            Iterator<FileDTO> fetchall = getDTOmaster().File().fetchall(curDir.getFullPath());
            return new BaseIterator<FileDTO, MGXFile>(fetchall) {
                @Override
                public MGXFile next() {
                    MGXFile f = FileDTOFactory.getInstance().toModel(iter.next());
                    f.setMaster(getMaster());
                    return f;
                }
            };
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public void update(MGXFile obj) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Task delete(MGXFile obj) {
        Task t = null;
        try {
            FileDTO dto = FileDTOFactory.getInstance().toDTO(obj);
            UUID uuid = getDTOmaster().File().delete(dto);
            t = getMaster().Task().get(obj, uuid, Task.TaskType.DELETE);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return t;
    }

    @Override
    public Iterator<MGXFile> fetchall() {
        return fetchall(MGXFile.getRoot(getMaster()));
    }

    public FileUploader createUploader(File localFile, MGXFile targetDir, String targetName) throws MGXClientException {
        assert targetDir.isDirectory();
        if (targetName.contains("/")) {
            assert false;
        }
        String fullPath = targetDir.getFullPath() + MGXFile.separator + targetName;
        try {
            return getDTOmaster().File().createUploader(localFile, fullPath);
        } catch (MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
