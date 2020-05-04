package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.TermAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.TermI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.TermDTO;
import de.cebitec.mgx.gui.dtoconversion.TermDTOFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author sjaenick
 */
public class TermAccess extends MasterHolder implements TermAccessI {

    public TermAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public List<TermI> byCategory(String cat) throws MGXException {
        List<TermI> ret = new ArrayList<>();
        try {
            for (TermDTO dto : getDTOmaster().Term().byCategory(cat)) {
                ret.add(TermDTOFactory.getInstance().toModel(getMaster(), dto));
            }
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        Collections.sort(ret);
        return ret;
    }

}
