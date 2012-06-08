package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.FileOrDirectory;
import de.cebitec.mgx.gui.datamodel.DirEntry;
import de.cebitec.mgx.gui.dtoconversion.DirEntryDTOFactory;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author sjaenick
 */
public class FileAccess extends AccessBase<DirEntry> {

    @Override
    public long create(DirEntry obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirEntry fetch(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DirEntry> fetchall() {
        List<DirEntry> ret = new ArrayList<>();
        try {
            for (FileOrDirectory fod : getDTOmaster().File().fetchall()) {
                DirEntry dirEntry = DirEntryDTOFactory.getInstance().toModel(fod);
                dirEntry.setMaster(this.getMaster());
                ret.add(dirEntry);
            }
        } catch (MGXServerException | MGXClientException ex) {
            Exceptions.printStackTrace(ex);
        }
        return ret;
    }

    @Override
    public void update(DirEntry obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
