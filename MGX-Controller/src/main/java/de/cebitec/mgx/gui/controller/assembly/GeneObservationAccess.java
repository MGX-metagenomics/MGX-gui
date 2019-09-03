/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.exception.MGXLoggedoutException;
import de.cebitec.mgx.api.model.assembly.GeneI;
import de.cebitec.mgx.api.model.assembly.GeneObservationI;
import de.cebitec.mgx.api.model.assembly.access.GeneObservationAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.GeneObservationDTO;
import de.cebitec.mgx.gui.dtoconversion.GeneObservationDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class GeneObservationAccess implements GeneObservationAccessI {

    private final MGXDTOMaster dtomaster;
    private final MGXMasterI master;

    public GeneObservationAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        this.dtomaster = dtomaster;
        this.master = master;
        if (master.isDeleted()) {
            throw new MGXLoggedoutException("You are disconnected.");
        }
    }

    @Override
    public Iterator<GeneObservationI> ByGene(GeneI c) throws MGXException {
        Iterator<GeneObservationDTO> it;
        try {
            it = getDTOmaster().GeneObservation().byGene(c.getId());
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

    private MGXDTOMaster getDTOmaster() {
        return dtomaster;
    }

    private MGXMasterI getMaster() {
        return master;
    }
}
