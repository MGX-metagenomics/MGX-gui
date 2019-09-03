/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.assembly.GeneCoverageI;
import de.cebitec.mgx.api.model.assembly.GeneI;
import de.cebitec.mgx.api.model.assembly.access.GeneCoverageAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.GeneCoverageDTO;
import de.cebitec.mgx.gui.controller.AccessBase;
import de.cebitec.mgx.gui.dtoconversion.GeneCoverageDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class GeneCoverageAccess extends AccessBase<GeneCoverageI> implements GeneCoverageAccessI {

    public GeneCoverageAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public Iterator<GeneCoverageI> ByGene(GeneI g) throws MGXException {
        Iterator<GeneCoverageDTO> it;
        try {
            it = getDTOmaster().GeneCoverage().byGene(g.getId());
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

    @Override
    public GeneCoverageI fetch(long id) throws MGXException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator<GeneCoverageI> fetchall() throws MGXException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(GeneCoverageI obj) throws MGXException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TaskI<GeneCoverageI> delete(GeneCoverageI obj) throws MGXException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
