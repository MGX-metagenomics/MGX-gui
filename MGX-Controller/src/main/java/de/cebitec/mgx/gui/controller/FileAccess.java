package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.dto.dto.FileOrDirectory;
import de.cebitec.mgx.gui.datamodel.DirEntry;
import de.cebitec.mgx.gui.dtoconversion.DirEntryDTOFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sjaenick
 */
public class FileAccess extends AccessBase<DirEntry> {

    @Override
    public Long create(DirEntry obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirEntry fetch(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DirEntry> fetchall() {
        List<DirEntry> ret = new ArrayList<DirEntry>();
        try {
            for (FileOrDirectory fod : getDTOmaster().File().fetchall()) {
                DirEntry dirEntry = DirEntryDTOFactory.getInstance().toModel(fod);
                dirEntry.setMaster(this.getMaster());
                ret.add(dirEntry);
            }
        } catch (MGXServerException ex) {
            Logger.getLogger(FileAccess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MGXClientException ex) {
            Logger.getLogger(FileAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    @Override
    public void update(DirEntry obj) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
