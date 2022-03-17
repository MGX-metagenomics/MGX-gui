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
import de.cebitec.mgx.api.model.assembly.GeneCoverageI;
import de.cebitec.mgx.api.model.assembly.access.GeneCoverageAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXClientLoggedOutException;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.GeneCoverageDTO;
import de.cebitec.mgx.gui.controller.MasterHolder;
import de.cebitec.mgx.gui.dtoconversion.GeneCoverageDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class GeneCoverageAccess extends MasterHolder implements GeneCoverageAccessI {

    public GeneCoverageAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public Iterator<GeneCoverageI> ByGene(RegionI g) throws MGXException {
        Iterator<GeneCoverageDTO> it;
        try {
            it = getDTOmaster().GeneCoverage().byGene(g.getId());
        } catch (MGXClientLoggedOutException mcle) {
            throw new MGXLoggedoutException(mcle);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<GeneCoverageDTO, GeneCoverageI>(it) {
            @Override
            public GeneCoverageI next() {
                return GeneCoverageDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
    }
}
