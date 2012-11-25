package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.upload.FileUploader;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.datamodel.ModelBase;
import de.cebitec.mgx.gui.dtoconversion.FileDTOFactory;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public List<MGXFile> fetchall(MGXFile rootDir) {
        List<MGXFile> ret = new ArrayList<>();
        try {
            for (FileDTO fod : getDTOmaster().File().fetchall(rootDir.getFullPath())) {
                MGXFile f = FileDTOFactory.getInstance().toModel(fod);
                f.setMaster(this.getMaster());
                f.setParent(rootDir);
                ret.add(f);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        Collections.sort(ret);
        return ret;
    }

    @Override
    public void update(MGXFile obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(MGXFile obj) {
        try {
            FileDTO dto = FileDTOFactory.getInstance().toDTO(obj);
            getDTOmaster().File().delete(dto);
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        obj.firePropertyChange(ModelBase.OBJECT_DELETED, obj, null);
    }

    @Override
    public List<MGXFile> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    public FileUploader createUploader(FileReader reader, MGXFile target) {
        return getDTOmaster().File().createUploader(target.getFullPath(), reader);
    }
}
