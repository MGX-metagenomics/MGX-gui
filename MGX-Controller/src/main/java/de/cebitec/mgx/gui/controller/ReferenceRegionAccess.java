package de.cebitec.mgx.gui.controller;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.access.ReferenceRegionAccessI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.MGXReferenceI;
import de.cebitec.mgx.api.model.ReferenceRegionI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.ReferenceRegionDTO;
import de.cebitec.mgx.gui.dtoconversion.ReferenceRegionDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;

/**
 *
 * @author belmann
 */
public class ReferenceRegionAccess extends MasterHolder implements ReferenceRegionAccessI {

    public ReferenceRegionAccess(MGXDTOMaster dtomaster, MGXMasterI master) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public Iterator<ReferenceRegionI> byReferenceInterval(MGXReferenceI ref, int from, int to) throws MGXException {
        Iterator<ReferenceRegionDTO> fetchall;
        try {
            fetchall = getDTOmaster().ReferenceRegion().byReferenceInterval(ref.getId(), from, to);
            return new BaseIterator<ReferenceRegionDTO, ReferenceRegionI>(fetchall) {
                @Override
                public ReferenceRegionI next() {
                    ReferenceRegionI s = ReferenceRegionDTOFactory.getInstance().toModel(getMaster(), iter.next());
                    return s;
                }
            };
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
    }

}
