/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cebitec.mgx.gui.controller.assembly;

import de.cebitec.mgx.api.MGXMasterI;
import de.cebitec.mgx.api.exception.MGXException;
import de.cebitec.mgx.api.misc.TaskI;
import de.cebitec.mgx.api.model.assembly.ContigI;
import de.cebitec.mgx.api.model.assembly.GeneI;
import de.cebitec.mgx.api.model.assembly.access.GeneAccessI;
import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.dto.dto.GeneDTO;
import de.cebitec.mgx.gui.controller.AccessBase;
import de.cebitec.mgx.gui.dtoconversion.GeneDTOFactory;
import de.cebitec.mgx.gui.util.BaseIterator;
import java.util.Iterator;

/**
 *
 * @author sj
 */
public class GeneAccess extends AccessBase<GeneI> implements GeneAccessI {

    public GeneAccess(MGXMasterI master, MGXDTOMaster dtomaster) throws MGXException {
        super(master, dtomaster);
    }

    @Override
    public GeneI fetch(long id) throws MGXException {
        GeneDTO dto = null;
        try {
            dto = getDTOmaster().Gene().fetch(id);
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return GeneDTOFactory.getInstance().toModel(getMaster(), dto);
    }

    @Override
    public Iterator<GeneI> fetchall() throws MGXException {
        Iterator<GeneDTO> it;
        try {
            it = getDTOmaster().Gene().fetchall();
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<GeneDTO, GeneI>(it) {
            @Override
            public GeneI next() {
                return GeneDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
    }

    @Override
    public Iterator<GeneI> ByContig(ContigI c) throws MGXException {
        Iterator<GeneDTO> it;
        try {
            it = getDTOmaster().Gene().byContig(c.getId());
        } catch (MGXDTOException ex) {
            throw new MGXException(ex);
        }
        return new BaseIterator<GeneDTO, GeneI>(it) {
            @Override
            public GeneI next() {
                return GeneDTOFactory.getInstance().toModel(getMaster(), iter.next());
            }
        };
    }

    @Override
    public void update(GeneI obj) throws MGXException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public TaskI<GeneI> delete(GeneI obj) throws MGXException {
        throw new UnsupportedOperationException("Not supported.");
    }
}
