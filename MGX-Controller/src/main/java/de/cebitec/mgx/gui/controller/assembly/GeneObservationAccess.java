/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.RegionI;
import de.cebitec.mgx.api.model.assembly.GeneObservationI;
import de.cebitec.mgx.api.model.assembly.access.GeneObservationAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.GeneObservationDTO;
import de.cebitec.mgx.gui.controller.MasterHolder;
import de.cebitec.mgx.gui.dtoconversion.GeneObservationDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class GeneObservationAccess extends MasterHolder implements GeneObservationAccessI {

    public GeneObservationAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public Iterator<GeneObservationI> ByGene(RegionI c) throws MGXException {
        Iterator<GeneObservationDTO> it;
        try {
            it = getDTOmaster().GeneObservation().byGene(c.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<GeneObservationDTO, GeneObservationI>(it) {
            @Override
            public GeneObservationI next() {
                return GeneObservationDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
    }
}
