package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.datatransfer.FileUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.datamodel.ModelBase;
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

    public long createDirectory(MGXFile newObj) {
        FileDTO dto = FileDTOFactory.getInstance().toDTO(newObj);
        try {
            return getDTOmaster().File().create(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return -1; // files have no id
    }

    @Override
    public MGXFile fetch(long id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    public Iterator<MGXFile> fetchall(final MGXFile rootDir) {
        try {
            Iterator<FileDTO> fetchall = getDTOmaster().File().fetchall(rootDir.getFullPath());
            return new BaseIterator<FileDTO, MGXFile>(fetchall) {
                @Override
                public MGXFile next() {
                    MGXFile f = FileDTOFactory.getInstance().toModel(iter.next());
                    f.setMaster(getMaster());
                    f.setParent(rootDir);
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Task delete(MGXFile obj) {
        try {
            FileDTO dto = FileDTOFactory.getInstance().toDTO(obj);
            getDTOmaster().File().delete(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.firePropertyChange(ModelBase.OBJECT_DELETED, obj, null);
        return null;
    }

    @Override
    public Iterator<MGXFile> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }

    public FileUploader createUploader(File localFile, MGXFile targetDir, String targetName) {
        assert targetDir.isDirectory();
        if (targetName.contains("/")) {
            assert false;
        }
        String fullPath = targetDir.getFullPath() + "/" + targetName;
        return getDTOmaster().File().createUploader(localFile, fullPath);
    }
}
