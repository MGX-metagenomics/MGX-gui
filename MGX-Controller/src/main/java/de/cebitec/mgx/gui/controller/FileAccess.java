package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.gui.datamodel.MGXFile;
import de.cebitec.mgx.gui.dtoconversion.FileDTOFactory;
import java.util.ArrayList;
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
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public MGXFile fetch(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        return ret;
    }

    @Override
    public void update(MGXFile obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(MGXFile obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<MGXFile> fetchall() {
        throw new UnsupportedOperationException("Not supported.");
    }
}
